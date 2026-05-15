"use client";

import { useState, useId } from "react";
import { calculateOrder, getCouponByCode } from "../api";
import type { LineItem, CustomerType, OrderResult } from "../types";
import LineItemRow from "./LineItemRow";
import PricingResult from "./PricingResult";

function createEmptyItem(): LineItem {
  return { id: crypto.randomUUID(), sku: "", price: "", quantity: "" };
}

function isValidItem(item: LineItem): boolean {
  const price = Number(item.price);
  const qty = Number(item.quantity);
  return (
    item.sku.trim() !== "" &&
    !isNaN(price) && price > 0 &&
    !isNaN(qty) && qty > 0 && Number.isInteger(qty)
  );
}

export default function OrderBuilder() {
  const [items, setItems] = useState<LineItem[]>([createEmptyItem()]);
  const [customerType, setCustomerType] = useState<CustomerType>("NORMAL");
  const [couponCode, setCouponCode] = useState("");
  const [appliedCoupon, setAppliedCoupon] = useState("");
  const [couponError, setCouponError] = useState<string | null>(null);
  const [couponLoading, setCouponLoading] = useState(false);
  const [result, setResult] = useState<OrderResult | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const couponInputId = useId();

  const allItemsValid = items.length > 0 && items.every(isValidItem);
  const canSubmit = allItemsValid && !loading;

  const handleItemChange = (id: string, field: keyof Omit<LineItem, "id">, value: string) => {
    setItems((prev) => prev.map((item) => (item.id === id ? { ...item, [field]: value } : item)));
  };

  const handleAddItem = () => setItems((prev) => [...prev, createEmptyItem()]);

  const handleRemoveItem = (id: string) => {
    setItems((prev) => prev.filter((item) => item.id !== id));
  };

  const handleApplyCoupon = async () => {
    const code = couponCode.trim();
    if (!code) return;
    setCouponError(null);
    setCouponLoading(true);
    try {
      const coupon = await getCouponByCode(code);
      if (!coupon || !coupon.active) {
        setCouponError(`Coupon "${code}" does not exist or is inactive.`);
        setAppliedCoupon("");
      } else {
        setAppliedCoupon(code);
      }
    } catch {
      setCouponError("Failed to validate coupon. Please try again.");
      setAppliedCoupon("");
    } finally {
      setCouponLoading(false);
    }
  };

  const handleSubmit = async () => {
    if (!canSubmit) return;
    setLoading(true);
    setError(null);
    setResult(null);
    try {
      const res = await calculateOrder({
        customerType,
        couponCode: appliedCoupon || undefined,
        items: items.map((i) => ({
          sku: i.sku.trim(),
          price: Number(i.price),
          quantity: Number(i.quantity),
        })),
      });
      setResult(res);
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "An unexpected error occurred.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="grid grid-cols-1 lg:grid-cols-[1fr_400px] gap-6 w-full">
      {/* Left panel – Order form */}
      <div className="space-y-6">
        {/* Customer type */}
        <section className="rounded-2xl border border-zinc-200 dark:border-zinc-700 bg-white dark:bg-zinc-900 p-6">
          <h2 className="text-base font-semibold text-zinc-900 dark:text-zinc-100 mb-4">Customer Details</h2>

          <div className="flex flex-col gap-2">
            <label className="text-xs font-medium text-zinc-500 uppercase tracking-wide">Customer Type</label>
            <div className="flex gap-2">
              {(["NORMAL", "VIP"] as CustomerType[]).map((type) => (
                <button
                  key={type}
                  type="button"
                  onClick={() => setCustomerType(type)}
                  className={`flex-1 rounded-lg border px-4 py-2 text-sm font-medium transition-colors ${
                    customerType === type
                      ? "border-indigo-500 bg-indigo-50 text-indigo-700 dark:bg-indigo-950 dark:text-indigo-300 dark:border-indigo-500"
                      : "border-zinc-300 dark:border-zinc-600 text-zinc-600 dark:text-zinc-400 hover:border-zinc-400 dark:hover:border-zinc-500"
                  }`}
                >
                  {type === "VIP" && (
                    <span className="mr-1.5">⭐</span>
                  )}
                  {type}
                </button>
              ))}
            </div>
          </div>
        </section>

        {/* Line items */}
        <section className="rounded-2xl border border-zinc-200 dark:border-zinc-700 bg-white dark:bg-zinc-900 p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-base font-semibold text-zinc-900 dark:text-zinc-100">Order Items</h2>
            <span className="text-xs text-zinc-400 dark:text-zinc-500">{items.length} item{items.length !== 1 ? "s" : ""}</span>
          </div>

          <div className="space-y-3">
            {items.map((item, index) => (
              <LineItemRow
                key={item.id}
                item={item}
                index={index}
                canRemove={items.length > 1}
                onChange={handleItemChange}
                onRemove={handleRemoveItem}
              />
            ))}
          </div>

          <button
            type="button"
            onClick={handleAddItem}
            className="mt-4 flex items-center gap-2 text-sm font-medium text-indigo-600 dark:text-indigo-400 hover:text-indigo-700 dark:hover:text-indigo-300 transition-colors"
          >
            <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
              <path fillRule="evenodd" d="M10 5a1 1 0 011 1v3h3a1 1 0 110 2h-3v3a1 1 0 11-2 0v-3H6a1 1 0 110-2h3V6a1 1 0 011-1z" clipRule="evenodd" />
            </svg>
            Add Item
          </button>
        </section>

        {/* Coupon */}
        <section className="rounded-2xl border border-zinc-200 dark:border-zinc-700 bg-white dark:bg-zinc-900 p-6">
          <h2 className="text-base font-semibold text-zinc-900 dark:text-zinc-100 mb-4">Coupon Code</h2>
          <div className="flex gap-2">
            <div className="flex flex-col gap-1 flex-1">
              <label htmlFor={couponInputId} className="sr-only">Coupon code</label>
              <input
                id={couponInputId}
                type="text"
                placeholder="Enter coupon code"
                value={couponCode}
                onChange={(e) => { setCouponCode(e.target.value); setCouponError(null); }}
                onKeyDown={(e) => e.key === "Enter" && handleApplyCoupon()}
                className="rounded-lg border border-zinc-300 dark:border-zinc-600 px-3 py-2 text-sm bg-white dark:bg-zinc-800 dark:text-zinc-100 focus:outline-none focus:ring-2 focus:ring-indigo-500 uppercase placeholder-normal"
              />
            </div>
            <button
              type="button"
              onClick={handleApplyCoupon}
              disabled={!couponCode.trim() || couponLoading}
              className="rounded-lg bg-zinc-800 dark:bg-zinc-200 px-4 py-2 text-sm font-medium text-white dark:text-zinc-900 hover:bg-zinc-700 dark:hover:bg-zinc-300 transition-colors disabled:opacity-40 disabled:cursor-not-allowed flex items-center gap-2"
            >
              {couponLoading && <span className="h-3.5 w-3.5 animate-spin rounded-full border-2 border-white/30 border-t-white dark:border-zinc-900/30 dark:border-t-zinc-900" />}
              Apply
            </button>
          </div>
          {couponError && (
            <div className="mt-2 flex items-center gap-2 text-sm text-amber-600 dark:text-amber-400">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4 shrink-0" viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
              </svg>
              <span>{couponError}</span>
            </div>
          )}
          {appliedCoupon && !couponError && (
            <div className="mt-2 flex items-center gap-2 text-sm text-green-600 dark:text-green-400">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4 shrink-0" viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
              </svg>
              <span>Coupon <strong>{appliedCoupon}</strong> will be applied</span>
                <button
                  type="button"
                  onClick={() => { setAppliedCoupon(""); setCouponCode(""); setCouponError(null); }}
                  className="ml-auto text-xs text-zinc-400 hover:text-red-500 transition-colors"
                >
                Remove
              </button>
            </div>
          )}
        </section>

        {/* Submit */}
        <button
          type="button"
          onClick={handleSubmit}
          disabled={!canSubmit}
          className="w-full rounded-xl bg-indigo-600 px-6 py-3 text-sm font-semibold text-white hover:bg-indigo-500 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
        >
          {loading && <span className="h-4 w-4 animate-spin rounded-full border-2 border-white/30 border-t-white" />}
          {loading ? "Calculating…" : "Calculate Price"}
        </button>
      </div>

      {/* Right panel – Results */}
      <div className="lg:sticky lg:top-6 self-start">
        <PricingResult result={result} loading={loading} error={error} />
      </div>
    </div>
  );
}

