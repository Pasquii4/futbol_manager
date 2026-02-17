import Link from "next/link"
import { LayoutDashboard, Users, Trophy, ShoppingBag, BarChart3, Settings } from "lucide-react"

export default function DashboardLayout({
    children,
}: {
    children: React.ReactNode
}) {
    return (
        <div className="flex h-screen bg-neutral-100 dark:bg-neutral-900">
            {/* Sidebar */}
            <aside className="w-64 bg-white dark:bg-neutral-950 border-r border-neutral-200 dark:border-neutral-800 hidden md:block">
                <div className="p-6">
                    <h1 className="text-2xl font-bold bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent">
                        Football Mgr
                    </h1>
                </div>
                <nav className="mt-6 px-4 space-y-2">
                    <NavItem href="/dashboard" icon={<LayoutDashboard size={20} />} label="Dashboard" />
                    <NavItem href="/ligas" icon={<Trophy size={20} />} label="Competiciones" />
                    <NavItem href="/equipos" icon={<Users size={20} />} label="Mis Equipos" />
                    <NavItem href="/mercado" icon={<ShoppingBag size={20} />} label="Mercado" />
                    <NavItem href="/estadisticas" icon={<BarChart3 size={20} />} label="Estadísticas" />
                    <div className="pt-4 mt-4 border-t border-neutral-200 dark:border-neutral-800">
                        <NavItem href="/settings" icon={<Settings size={20} />} label="Configuración" />
                    </div>
                </nav>
            </aside>

            {/* Main Content */}
            <main className="flex-1 overflow-y-auto">
                <header className="h-16 bg-white dark:bg-neutral-950 border-b border-neutral-200 dark:border-neutral-800 flex items-center justify-between px-8">
                    <h2 className="text-lg font-semibold text-neutral-800 dark:text-neutral-200">
                        Dashboard
                    </h2>
                    <div className="flex items-center gap-4">
                        {/* Profile / User Menu placeholder */}
                        <div className="h-8 w-8 rounded-full bg-neutral-200 dark:bg-neutral-800" />
                    </div>
                </header>
                <div className="p-8">
                    {children}
                </div>
            </main>
        </div>
    )
}

function NavItem({ href, icon, label }: { href: string; icon: React.ReactNode; label: string }) {
    return (
        <Link
            href={href}
            className="flex items-center gap-3 px-4 py-3 text-neutral-600 dark:text-neutral-400 hover:bg-neutral-50 dark:hover:bg-neutral-900 hover:text-blue-600 dark:hover:text-blue-400 rounded-lg transition-colors"
        >
            {icon}
            <span className="font-medium">{label}</span>
        </Link>
    )
}
