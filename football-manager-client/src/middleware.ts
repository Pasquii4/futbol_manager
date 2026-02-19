
import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

export function middleware(request: NextRequest) {
    const token = request.cookies.get('token')?.value;
    const { pathname } = request.nextUrl;

    // Paths that require authentication
    const protectedPaths = [
        '/dashboard',
        '/laliga',
        '/matches',
        '/teams',
        '/market',
        '/lineup',
        '/estadisticas',
        '/settings'
    ];

    // Check if current path is protected
    const isProtected = protectedPaths.some(path => pathname.startsWith(path));

    if (isProtected && !token) {
        return NextResponse.redirect(new URL('/login', request.url));
    }

    // Redirect to dashboard if already logged in and trying to access auth pages
    if (token && (pathname === '/login' || pathname === '/register')) {
        return NextResponse.redirect(new URL('/dashboard', request.url));
    }

    return NextResponse.next();
}

export const config = {
    matcher: [
        '/dashboard/:path*',
        '/laliga/:path*',
        '/matches/:path*',
        '/teams/:path*',
        '/market/:path*',
        '/lineup/:path*',
        '/estadisticas/:path*',
        '/settings/:path*',
        '/login',
        '/register'
    ],
};
