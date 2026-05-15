import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import Link from "next/link";
import NavLinks from "../src/components/NavLinks";
import "./globals.css";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "Pricing Engine",
  description: "Pricing Engine UI",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html
      lang="en"
      className={`${geistSans.variable} ${geistMono.variable} h-full antialiased`}
    >
      <body className="min-h-full flex flex-col bg-zinc-50 dark:bg-zinc-950 text-zinc-900 dark:text-zinc-100">
        <header className="sticky top-0 z-40 w-full border-b border-zinc-200 dark:border-zinc-800 bg-white/80 dark:bg-zinc-900/80 backdrop-blur-md">
          <div className="mx-auto flex h-14 max-w-5xl items-center gap-6 px-4 sm:px-6">
            <Link
              href="/"
              className="flex items-center gap-2 font-semibold text-zinc-900 dark:text-zinc-100 text-sm"
            >
              <span className="flex h-7 w-7 items-center justify-center rounded-lg bg-indigo-600 text-white text-xs font-bold">
                PE
              </span>
              Pricing Engine
            </Link>

            <NavLinks />
          </div>
        </header>

        <main className="flex-1 mx-auto w-full max-w-5xl px-4 sm:px-6 py-8">
          {children}
        </main>
      </body>
    </html>
  );
}
