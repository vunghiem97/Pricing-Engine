import CouponsList from "../../src/components/CouponsList";

export default function CouponsPage() {
  return (
    <div>
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-zinc-900 dark:text-zinc-100">Coupons</h1>
        <p className="mt-1 text-sm text-zinc-500 dark:text-zinc-400">
          All configured coupon codes. Click <strong>View details</strong> to inspect any coupon.
        </p>
      </div>
      <CouponsList />
    </div>
  );
}

