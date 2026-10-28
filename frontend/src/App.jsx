import { useEffect, useState } from 'react'
import { addProfile, getProfiles, refreshProfile } from './api'

function formatDate(dateString) {
  if (!dateString) return 'Never'
  try {
    const date = new Date(dateString)
    return date.toLocaleString()
  } catch {
    return dateString
  }
}

function RatingChart({ history }) {
  if (!history || history.length === 0) {
    return <div className="chart-empty">No rating history available</div>
  }

  const sorted = [...history].sort((a, b) => (a.ratingUpdateTimeSeconds || 0) - (b.ratingUpdateTimeSeconds || 0))
  const ratings = sorted.map(h => h.newRating).filter(r => r !== null && r !== undefined)
  if (ratings.length === 0) {
    return <div className="chart-empty">No rating history available</div>
  }

  const min = Math.min(...ratings)
  const max = Math.max(...ratings)
  const range = max - min || 100
  const width = 300
  const height = 100
  const padding = 10

  const points = ratings
    .map((rating, idx) => {
      const x = padding + (idx / (ratings.length - 1)) * (width - 2 * padding)
      const y = height - padding - ((rating - min) / range) * (height - 2 * padding)
      return `${x},${y}`
    })
    .join(' ')

  return (
    <svg className="rating-chart" viewBox={`0 0 ${width} ${height}`} preserveAspectRatio="none">
      <polyline points={points} fill="none" stroke="#666" strokeWidth="1.5" />
    </svg>
  )
}

export default function App() {
  const [handle, setHandle] = useState('')
  const [profiles, setProfiles] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [addMsg, setAddMsg] = useState('')
  const [adding, setAdding] = useState(false)
  const [refreshingAll, setRefreshingAll] = useState(false)

  async function load() {
    setLoading(true)
    setError('')
    try {
      const data = await getProfiles()
      setProfiles(data || [])
    } catch (e) {
      setError('Failed to load profiles')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [])

  async function onAdd(e) {
    e.preventDefault()
    const h = handle.trim()
    if (!h) {
      setAddMsg('Please enter a username')
      return
    }
    setAdding(true)
    setAddMsg('')
    try {
      await addProfile(h)
      setHandle('')
      setAddMsg('Profile added successfully')
      await load()
    } catch (e) {
      setAddMsg('Failed to add profile. Check the username and try again.')
    } finally {
      setAdding(false)
    }
  }

  async function onRefresh(h) {
    try {
      await refreshProfile(h)
      await load()
    } catch (e) {
      alert('Failed to refresh profile')
    }
  }

  async function onRefreshAll() {
    setRefreshingAll(true)
    try {
      for (const p of profiles) {
        await refreshProfile(p.handle)
      }
      await load()
    } catch (e) {
      // ignore
    } finally {
      setRefreshingAll(false)
    }
  }

  return (
    <main className="container">
      <header className="header">
        <h1>CP Forge</h1>
        <p className="subtitle">Competitive Programming Dashboard</p>
      </header>

      <section className="add-profile">
        <form className="input-group" onSubmit={onAdd}>
          <input
            value={handle}
            onChange={(e) => setHandle(e.target.value)}
            placeholder="Enter Codeforces username"
            autoComplete="off"
          />
          <button className="btn" disabled={adding}>{adding ? 'Adding...' : 'Add Profile'}</button>
        </form>
        {addMsg && <p className="message">{addMsg}</p>}
      </section>

      <section className="profiles">
        <div className="section-header">
          <h2>Saved Profiles</h2>
          <button className="btn btn-secondary" onClick={onRefreshAll} disabled={refreshingAll || profiles.length === 0}>
            {refreshingAll ? 'Refreshing...' : 'Refresh'}
          </button>
        </div>
        {loading && <div className="state">Loading...</div>}
        {!loading && error && <div className="state">{error}</div>}
        {!loading && !error && profiles.length === 0 && <div className="state">No profiles added yet. Add a Codeforces username above.</div>}
        <div className="profiles-list">
          {profiles.map((profile) => (
            <div className="profile-card" key={profile.id || profile.handle}>
              <div className="profile-header">
                <div className="profile-info">
                  <h3>{profile.handle}</h3>
                  <div className="profile-meta">
                    {profile.rank || 'Unrated'}
                    {profile.country ? ` • ${profile.country}` : ''}
                    {profile.organization ? ` • ${profile.organization}` : ''}
                  </div>
                </div>
                <button className="btn btn-secondary" onClick={() => onRefresh(profile.handle)}>Refresh</button>
              </div>

              <div className="profile-stats">
                <div className="stat">
                  <span className="stat-label">Current Rating</span>
                  <span className="stat-value">{profile.rating ?? 'N/A'}</span>
                </div>
                <div className="stat">
                  <span className="stat-label">Peak Rating</span>
                  <span className="stat-value">{profile.maxRating ?? 'N/A'}</span>
                </div>
                <div className="stat">
                  <span className="stat-label">Max Rank</span>
                  <span className="stat-value">{profile.maxRank || 'N/A'}</span>
                </div>
              </div>

              <div className="sync-info">Last synchronized: {formatDate(profile.lastSyncedAt)}</div>

              <div className="chart-container">
                <div className="chart-title">Rating History</div>
                <RatingChart history={profile.ratingHistory} />
              </div>
            </div>
          ))}
        </div>
      </section>
    </main>
  )
}
