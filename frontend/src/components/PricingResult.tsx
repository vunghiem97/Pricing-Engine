"use client";

import type { OrderResult } from "../types";

interface Props {
  result: OrderResult | null;
  loading: boolean;
  error: string | null;
}

export default function PricingResult({ result, loading, error }: Props) {
  if (loading) {
    return (
      <div className="rounded-2xl border border-zinc-200 dark:border-zinc-700 bg-white dark:bg-zinc-900 p-6 flex flex-col items-center justify-center gap-3 min-h-[200px]">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-zinc-200 border-t-indigo-500" />
        <p className="text-sm text-zinc-500">Calculating…</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-2xl border border-red-200 dark:border-red-800 bg-red-50 dark:bg-red-950/40 p-6">
        <div className="flex items-start gap-3">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            className="h-5 w-5 mt-0.5 text-red-500 shrink-0"
            viewBox="0 0 20 20"
            fill="currentColor"
          >
            <path
              fillRule="evenodd"
              d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z"
              clipRule="evenodd"
            />
          </svg>
          <div>
            <p className="text-sm font-medium text-red-700 dark:text-red-400">Calculation failed</p>
            <p className="text-sm text-red-600 dark:text-red-300 mt-1">{error}</p>
          </div>
        </div>
      </div>
    );
  }

  if (!result) {
    return (
      <div className="rounded-2xl border border-dashed border-zinc-300 dark:border-zinc-700 bg-zinc-50 dark:bg-zinc-900/50 p-6 flex flex-col items-center justify-center gap-2 min-h-[200px]">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          className="h-10 w-10 text-zinc-300 dark:text-zinc-600"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={1.5}
            d="M9 7h6m0 10v-3m-3 3h.01M9 17h.01M9 11h.01M12 11h.01M15 11h.01M4 19h16a2 2 0 002-2V7a2 2 0 00-2-2H4a2 2 0 00-2 2v10a2 2 0 002 2z"
          />
        </svg>
        <p className="text-sm text-zinc-400 dark:text-zinc-500">
          Add items and click <strong>Calculate Price</strong> to see results
        </p>
      </div>
    );
  }

  const fmt = (n: number) =>
    new Intl.NumberFormat("en-US", { style: "currency", currency: "USD" }).format(n);

  const typeLabel: Record<string, string> = {
    PERCENTAGE_DISCOUNT: "% Discount",
    BUY_X_GET_Y: "Buy X Get Y",
    VIP_DISCOUNT: "VIP",
    COUPON: "Coupon",
  };

  return (
    <div className="rounded-2xl border border-zinc-200 dark:border-zinc-700 bg-white dark:bg-zinc-900 overflow-hidden">
      <div className="px-6 py-4 border-b border-zinc-100 dark:border-zinc-800">
        <h2 className="text-base font-semibold text-zinc-900 dark:text-zinc-100">Pricing Breakdown</h2>
      </div>

      <div className="px-6 py-4 space-y-3">
        {/* Subtotal */}
        <div className="flex justify-between text-sm">
          <span className="text-zinc-500 dark:text-zinc-400">Subtotal</span>
          <span className="font-medium text-zinc-900 dark:text-zinc-100">{fmt(result.subtotal)}</span>
        </div>

        {/* Discount breakdown */}
        {result.discountBreakdown.length > 0 && (
          <div className="space-y-2">
            <p className="text-xs font-medium uppercase tracking-wide text-zinc-400 dark:text-zinc-500">
              Applied Discounts
            </p>
            {result.discountBreakdown.map((d, i) => (
              <div key={i} className="flex justify-between items-start gap-2">
                <div className="flex items-center gap-1.5 flex-wrap">
                  <span className="inline-flex items-center rounded-full bg-green-100 dark:bg-green-900/40 px-2 py-0.5 text-xs font-medium text-green-700 dark:text-green-400">
                    {d.promotionName}
                  </span>
                  <span className="inline-flex items-center rounded-md bg-zinc-100 dark:bg-zinc-800 px-1.5 py-0.5 text-xs text-zinc-500 dark:text-zinc-400">
                    {typeLabel[d.promotionType] ?? d.promotionType}
                  </span>
                </div>
                <span className="text-sm font-medium text-green-600 dark:text-green-400 shrink-0">
                  −{fmt(d.discountAmount)}
                </span>
              </div>
            ))}

            {/* Total discount summary */}
            <div className="flex justify-between text-sm border-t border-dashed border-zinc-200 dark:border-zinc-700 pt-2">
              <span className="text-zinc-500 dark:text-zinc-400">Total savings</span>
              <span className="font-medium text-green-600 dark:text-green-400">−{fmt(result.discount)}</span>
            </div>
          </div>
        )}

        {result.discountBreakdown.length === 0 && (
          <p className="text-sm text-zinc-400 dark:text-zinc-500 italic">No discounts applied</p>
        )}
      </div>

      {/* Final price */}
      <div className="px-6 py-4 bg-indigo-50 dark:bg-indigo-950/30 border-t border-indigo-100 dark:border-indigo-900">
        <div className="flex justify-between items-baseline">
          <span className="text-sm font-semibold text-indigo-900 dark:text-indigo-300">Final Price</span>
          <span className="text-2xl font-bold text-indigo-600 dark:text-indigo-400">{fmt(result.finalPrice)}</span>
        </div>
      </div>
    </div>
  );
}
