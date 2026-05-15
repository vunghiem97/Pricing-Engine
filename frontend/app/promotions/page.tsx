import PromotionsList from "../../src/components/PromotionsList";

export default function PromotionsPage() {
  return (
    <div>
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-zinc-900 dark:text-zinc-100">Promotions</h1>
        <p className="mt-1 text-sm text-zinc-500 dark:text-zinc-400">
          All configured promotion rules. Click <strong>View details</strong> to inspect any rule.
        </p>
      </div>
      <PromotionsList />
    </div>
  );
}

