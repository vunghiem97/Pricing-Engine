"use client";

import type { LineItem } from "../types";

interface Props {
  item: LineItem;
  index: number;
  canRemove: boolean;
  onChange: (id: string, field: keyof Omit<LineItem, "id">, value: string) => void;
  onRemove: (id: string) => void;
}

export default function LineItemRow({ item, index, canRemove, onChange, onRemove }: Props) {
  const skuError = item.sku.trim() === "" ? "SKU is required" : "";
  const priceError =
    item.price !== "" && (isNaN(Number(item.price)) || Number(item.price) <= 0)
      ? "Must be > 0"
      : "";
  const qtyError =
    item.quantity !== "" &&
    (isNaN(Number(item.quantity)) || Number(item.quantity) <= 0 || !Number.isInteger(Number(item.quantity)))
      ? "Must be a positive integer"
      : "";

  return (
    <div className="grid grid-cols-[1fr_1fr_1fr_auto] gap-3 items-start">
      {/* SKU */}
      <div className="flex flex-col gap-1">
        {index === 0 && <label className="text-xs font-medium text-zinc-500 uppercase tracking-wide">SKU</label>}
        <input
          type="text"
          placeholder="e.g. ITEM-001"
          value={item.sku}
          onChange={(e) => onChange(item.id, "sku", e.target.value)}
          className={`rounded-lg border px-3 py-2 text-sm bg-white dark:bg-zinc-800 dark:text-zinc-100 focus:outline-none focus:ring-2 focus:ring-indigo-500 ${
            skuError && item.sku !== "" ? "border-red-400" : "border-zinc-300 dark:border-zinc-600"
          }`}
        />
        {skuError && item.sku !== "" && <p className="text-xs text-red-500">{skuError}</p>}
      </div>

      {/* Price */}
      <div className="flex flex-col gap-1">
        {index === 0 && <label className="text-xs font-medium text-zinc-500 uppercase tracking-wide">Unit Price</label>}
        <input
          type="number"
          min="0.01"
          step="0.01"
          placeholder="0.00"
          value={item.price}
          onChange={(e) => onChange(item.id, "price", e.target.value)}
          className={`rounded-lg border px-3 py-2 text-sm bg-white dark:bg-zinc-800 dark:text-zinc-100 focus:outline-none focus:ring-2 focus:ring-indigo-500 ${
            priceError ? "border-red-400" : "border-zinc-300 dark:border-zinc-600"
          }`}
        />
        {priceError && <p className="text-xs text-red-500">{priceError}</p>}
      </div>

      {/* Quantity */}
      <div className="flex flex-col gap-1">
        {index === 0 && <label className="text-xs font-medium text-zinc-500 uppercase tracking-wide">Quantity</label>}
        <input
          type="number"
          min="1"
          step="1"
          placeholder="1"
          value={item.quantity}
          onChange={(e) => onChange(item.id, "quantity", e.target.value)}
          className={`rounded-lg border px-3 py-2 text-sm bg-white dark:bg-zinc-800 dark:text-zinc-100 focus:outline-none focus:ring-2 focus:ring-indigo-500 ${
            qtyError ? "border-red-400" : "border-zinc-300 dark:border-zinc-600"
          }`}
        />
        {qtyError && <p className="text-xs text-red-500">{qtyError}</p>}
      </div>

      {/* Remove */}
      <div className={`flex flex-col ${index === 0 ? "pt-5" : ""}`}>
        <button
          type="button"
          disabled={!canRemove}
          onClick={() => onRemove(item.id)}
          className="p-2 rounded-lg text-zinc-400 hover:text-red-500 hover:bg-red-50 dark:hover:bg-red-950 transition-colors disabled:opacity-30 disabled:cursor-not-allowed"
          title="Remove item"
        >
          <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
            <path
              fillRule="evenodd"
              d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z"
              clipRule="evenodd"
            />
          </svg>
        </button>
      </div>
    </div>
  );
}

