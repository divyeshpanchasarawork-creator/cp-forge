const API_BASE = import.meta.env.VITE_API_BASE_URL || ''

async function request(path, options = {}) {
  const url = path.startsWith('http') ? path : `${API_BASE}${path}`
  const res = await fetch(url, {
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {})
    },
    ...options
  })
  if (!res.ok) {
    throw new Error(`Request failed: ${res.status}`)
  }
  const ct = res.headers.get('content-type') || ''
  if (ct.includes('application/json')) {
    return res.json()
  }
  return null
}

export function getProfiles() {
  return request('/api/codeforces/profiles')
}

export function addProfile(handle) {
  return request('/api/codeforces/profiles', {
    method: 'POST',
    body: JSON.stringify({ handle })
  })
}

export function refreshProfile(handle) {
  return request(`/api/codeforces/profiles/${encodeURIComponent(handle)}/refresh`, {
    method: 'POST'
  })
}
