export type CustomerType = "NORMAL" | "VIP";

export interface LineItem {
  id: string;
  sku: string;
  price: string;
  quantity: string;
}

export interface OrderRequest {
  customerType: CustomerType;
  couponCode?: string;
  items: { sku: string; price: number; quantity: number }[];
}

// ── Products ────────────────────────────────────────────────────────────────
export interface Product {
  id: number;
  sku: string;
  name: string;
  price: number;
  stock: number;
}

// ── Promotions ───────────────────────────────────────────────────────────────
export type PromotionType =
  | "PERCENTAGE_DISCOUNT"
  | "BUY_X_GET_Y"
  | "VIP_DISCOUNT"
  | "COUPON";

export interface Promotion {
  id: number;
  name: string;
  type: PromotionType;
  value: number;
  freeQuantity: number | null;
  active: boolean;
}

// ── Coupons ──────────────────────────────────────────────────────────────────
export interface Coupon {
  id: number;
  code: string;
  discountAmount: number;
  active: boolean;
}

// ── Orders ───────────────────────────────────────────────────────────────────
export interface DiscountBreakdownItem {
  promotionName: string;
  promotionType: PromotionType;
  discountAmount: number;
}

export interface OrderResult {
  subtotal: number;
  discount: number;
  finalPrice: number;
  discountBreakdown: DiscountBreakdownItem[];
}
