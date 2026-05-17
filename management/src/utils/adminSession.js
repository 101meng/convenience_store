const TOKEN_KEY = 'admin_token'
const PROFILE_KEY = 'admin_profile'

export function loadAdminToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

export function loadAdminProfile() {
  const raw = localStorage.getItem(PROFILE_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw)
  } catch (error) {
    clearAdminSession()
    return null
  }
}

export function loadAdminSession() {
  const token = loadAdminToken()
  const profile = loadAdminProfile()
  return {
    token,
    profile,
    role: profile?.role || '',
    storeId: profile?.storeId ?? null
  }
}

export function saveAdminSession(token, profile) {
  localStorage.setItem(TOKEN_KEY, token)
  localStorage.setItem(PROFILE_KEY, JSON.stringify(profile))
}

export function clearAdminSession() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(PROFILE_KEY)
}
