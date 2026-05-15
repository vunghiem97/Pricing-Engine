import type { OrderRequest, OrderResult, Promotion, Product, Coupon } from "./types";

const BASE_URL = "http://localhost:8080";

export const calculateOrder = async (data: OrderRequest): Promise<OrderResult> => {
    const res = await fetch(`${BASE_URL}/api/orders/calculate`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
    });

    if (!res.ok) {
        let message = `Request failed with status ${res.status}`;
        try {
            const body = await res.json();
            message = body.message ?? body.error ?? message;
        } catch {
            // ignore parse errors
        }
        throw new Error(message);
    }

    return res.json();
};

export const getPromotions = async (): Promise<Promotion[]> => {
    const res = await fetch(`${BASE_URL}/api/promotions`);
    if (!res.ok) throw new Error(`Failed to load promotions (${res.status})`);
    return res.json();
};

export const getActivePromotions = async (): Promise<Promotion[]> => {
    const res = await fetch(`${BASE_URL}/api/promotions/active`);
    if (!res.ok) throw new Error(`Failed to load active promotions (${res.status})`);
    return res.json();
};

export const getPromotionById = async (id: string): Promise<Promotion> => {
    const res = await fetch(`${BASE_URL}/api/promotions/${id}`);
    if (!res.ok) throw new Error(`Failed to load promotion (${res.status})`);
    return res.json();
};

export const getProducts = async (): Promise<Product[]> => {
    const res = await fetch(`${BASE_URL}/api/products`);
    if (!res.ok) throw new Error(`Failed to load products (${res.status})`);
    return res.json();
};

export const getProductById = async (id: string): Promise<Product> => {
    const res = await fetch(`${BASE_URL}/api/products/${id}`);
    if (!res.ok) throw new Error(`Failed to load product (${res.status})`);
    return res.json();
};

export const getProductBySku = async (sku: string): Promise<Product> => {
    const res = await fetch(`${BASE_URL}/api/products/sku/${encodeURIComponent(sku)}`);
    if (!res.ok) throw new Error(`Failed to load product (${res.status})`);
    return res.json();
};

export const getCoupons = async (): Promise<Coupon[]> => {
    const res = await fetch(`${BASE_URL}/api/coupons`);
    if (!res.ok) throw new Error(`Failed to load coupons (${res.status})`);
    return res.json();
};

export const getActiveCoupons = async (): Promise<Coupon[]> => {
    const res = await fetch(`${BASE_URL}/api/coupons/active`);
    if (!res.ok) throw new Error(`Failed to load active coupons (${res.status})`);
    return res.json();
};

export const getCouponById = async (id: string): Promise<Coupon> => {
    const res = await fetch(`${BASE_URL}/api/coupons/${id}`);
    if (!res.ok) throw new Error(`Failed to load coupon (${res.status})`);
    return res.json();
};

export const getCouponByCode = async (code: string): Promise<Coupon | null> => {
    const res = await fetch(`${BASE_URL}/api/coupons/code/${encodeURIComponent(code)}`);
    if (res.status === 404) return null;
    if (!res.ok) throw new Error(`Failed to validate coupon (${res.status})`);
    return res.json();
};
