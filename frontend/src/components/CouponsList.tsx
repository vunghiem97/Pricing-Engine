"use client";

import { useEffect, useState } from "react";
import { getCoupons } from "../api";
import type { Coupon } from "../types";

function StatusBadge({ active }: { active: boolean }) {
  return (
    <span className={`inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium ${
      active
        ? "bg-green-100 text-green-700 dark:bg-green-900/40 dark:text-green-400"
        : "bg-zinc-100 text-zinc-500 dark:bg-zinc-800 dark:text-zinc-400"
    }`}>
      {active ? "Active" : "Inactive"}
    </span>
  );
}

function DetailRow({ label, value }: { label: string; value: React.ReactNode }) {
  return (
    <div className="grid grid-cols-[160px_1fr] gap-2 py-1.5 border-b border-zinc-100 dark:border-zinc-800 last:border-0">
      <span className="text-xs text-zinc-500 dark:text-zinc-400 font-medium">{label}</span>
      <span className="text-sm text-zinc-800 dark:text-zinc-200 break-words">{value}</span>
    </div>
  );
}

const fmt = (n: number) =>
  new Intl.NumberFormat("en-US", { style: "currency", currency: "USD" }).format(n);

function CouponDetail({ coupon, onClose }: { coupon: Coupon; onClose: () => void }) {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40 backdrop-blur-sm" onClick={onClose}>
      <div className="w-full max-w-md rounded-2xl bg-white dark:bg-zinc-900 shadow-2xl overflow-hidden" onClick={(e) => e.stopPropagation()}>
        <div className="flex items-start justify-between px-6 py-4 border-b border-zinc-100 dark:border-zinc-800">
          <div className="flex items-center gap-2">
            <code className="text-base font-bold font-mono text-zinc-900 dark:text-zinc-100">{coupon.code}</code>
            <StatusBadge active={coupon.active} />
          </div>
          <button type="button" onClick={onClose} className="p-1 rounded-lg text-zinc-400 hover:text-zinc-600 dark:hover:text-zinc-200 hover:bg-zinc-100 dark:hover:bg-zinc-800 transition-colors">
            <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
              <path fillRule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clipRule="evenodd" />
            </svg>
          </button>
        </div>
        <div className="px-6 py-4 space-y-0.5">
          <DetailRow label="ID" value={<code className="font-mono text-xs">{coupon.id}</code>} />
          <DetailRow label="Code" value={<code className="font-mono font-semibold">{coupon.code}</code>} />
          <DetailRow label="Discount Amount" value={fmt(coupon.discountAmount)} />
          <DetailRow label="Status" value={<StatusBadge active={coupon.active} />} />
        </div>
      </div>
    </div>
  );
}

export default function CouponsList() {
  const [coupons, setCoupons] = useState<Coupon[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selected, setSelected] = useState<Coupon | null>(null);
  const [filter, setFilter] = useState<"ALL" | "ACTIVE" | "INACTIVE">("ALL");

  useEffect(() => {
    getCoupons()
      .then(setCoupons)
      .catch((e: unknown) => setError(e instanceof Error ? e.message : "Failed to load coupons"))
      .finally(() => setLoading(false));
  }, []);

  const filtered = coupons.filter((c) => {
    if (filter === "ACTIVE") return c.active;
    if (filter === "INACTIVE") return !c.active;
    return true;
  });

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center gap-3 py-24">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-zinc-200 border-t-indigo-500" />
        <p className="text-sm text-zinc-500">Loading coupons…</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-2xl border border-red-200 dark:border-red-800 bg-red-50 dark:bg-red-950/40 p-6">
        <div className="flex items-start gap-3">
          <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mt-0.5 text-red-500 shrink-0" viewBox="0 0 20 20" fill="currentColor">
            <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
          </svg>
          <div>
            <p className="text-sm font-medium text-red-700 dark:text-red-400">Failed to load coupons</p>
            <p className="text-sm text-red-600 dark:text-red-300 mt-1">{error}</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <>
      {selected && <CouponDetail coupon={selected} onClose={() => setSelected(null)} />}

      <div className="space-y-4">
        {/* Filter tabs */}
        <div className="flex items-center gap-1 p-1 rounded-xl bg-zinc-100 dark:bg-zinc-800 w-fit">
          {(["ALL", "ACTIVE", "INACTIVE"] as const).map((tab) => (
            <button
              key={tab}
              type="button"
              onClick={() => setFilter(tab)}
              className={`rounded-lg px-4 py-1.5 text-sm font-medium transition-colors ${
                filter === tab
                  ? "bg-white dark:bg-zinc-700 text-zinc-900 dark:text-zinc-100 shadow-sm"
                  : "text-zinc-500 dark:text-zinc-400 hover:text-zinc-700 dark:hover:text-zinc-200"
              }`}
            >
              {tab}
              <span className="ml-1.5 text-xs text-zinc-400 dark:text-zinc-500">
                {tab === "ALL" ? coupons.length : coupons.filter((c) => (tab === "ACTIVE" ? c.active : !c.active)).length}
              </span>
            </button>
          ))}
        </div>

        {filtered.length === 0 ? (
          <div className="rounded-2xl border border-dashed border-zinc-300 dark:border-zinc-700 p-12 text-center">
            <p className="text-zinc-400 dark:text-zinc-500 text-sm">No coupons found.</p>
          </div>
        ) : (
          <div className="overflow-hidden rounded-2xl border border-zinc-200 dark:border-zinc-700">
            <table className="w-full text-sm">
              <thead>
                <tr className="bg-zinc-50 dark:bg-zinc-800/50 text-left">
                  <th className="px-4 py-3 text-xs font-semibold uppercase tracking-wide text-zinc-500 dark:text-zinc-400">Code</th>
                  <th className="px-4 py-3 text-xs font-semibold uppercase tracking-wide text-zinc-500 dark:text-zinc-400 hidden sm:table-cell">Discount Amount</th>
                  <th className="px-4 py-3 text-xs font-semibold uppercase tracking-wide text-zinc-500 dark:text-zinc-400">Status</th>
                  <th className="px-4 py-3" />
                </tr>
              </thead>
              <tbody className="divide-y divide-zinc-100 dark:divide-zinc-800">
                {filtered.map((coupon) => (
                  <tr key={coupon.id} className="hover:bg-zinc-50 dark:hover:bg-zinc-800/40 transition-colors">
                    <td className="px-4 py-3">
                      <code className="font-mono font-semibold text-zinc-900 dark:text-zinc-100">{coupon.code}</code>
                    </td>
                    <td className="px-4 py-3 text-zinc-700 dark:text-zinc-300 hidden sm:table-cell">
                      {fmt(coupon.discountAmount)}
                    </td>
                    <td className="px-4 py-3">
                      <StatusBadge active={coupon.active} />
                    </td>
                    <td className="px-4 py-3 text-right">
                      <button
                        type="button"
                        onClick={() => setSelected(coupon)}
                        className="text-xs font-medium text-indigo-600 dark:text-indigo-400 hover:text-indigo-700 dark:hover:text-indigo-300 transition-colors"
                      >
                        View details
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </>
  );
}
