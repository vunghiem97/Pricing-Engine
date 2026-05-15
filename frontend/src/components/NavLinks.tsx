"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

const links = [
  { href: "/", label: "Order Builder" },
  { href: "/promotions", label: "Promotions" },
  { href: "/coupons", label: "Coupons" },
];

export default function NavLinks() {
  const pathname = usePathname();

  return (
    <nav className="flex items-center gap-1 ml-2">
      {links.map(({ href, label }) => {
        const isActive = href === "/" ? pathname === "/" : pathname.startsWith(href);
        return (
          <Link
            key={href}
            href={href}
            className={`rounded-lg px-3 py-1.5 text-sm font-medium transition-colors ${
              isActive
                ? "bg-indigo-50 dark:bg-indigo-950/40 text-indigo-700 dark:text-indigo-400"
                : "text-zinc-600 dark:text-zinc-400 hover:bg-zinc-100 dark:hover:bg-zinc-800 hover:text-zinc-900 dark:hover:text-zinc-100"
            }`}
          >
            {label}
          </Link>
        );
      })}
    </nav>
  );
}

