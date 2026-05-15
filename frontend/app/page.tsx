import OrderBuilder from "../src/components/OrderBuilder";

export default function HomePage() {
  return (
    <div>
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-zinc-900 dark:text-zinc-100">Order Builder</h1>
        <p className="mt-1 text-sm text-zinc-500 dark:text-zinc-400">
          Build your order, apply a coupon, and calculate the final price with promotions.
        </p>
      </div>
      <OrderBuilder />
    </div>
  );
}
