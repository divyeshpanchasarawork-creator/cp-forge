let profiles = [];

function formatDate(dateString) {
    if (!dateString) return 'Never';
    try {
        const date = new Date(dateString);
        return date.toLocaleString();
    } catch {
        return dateString;
    }
}

function renderRatingChart(history) {
    if (!history || history.length === 0) {
        return '<div style="color: #666; font-size: 13px;">No rating history available</div>';
    }
    
    const sortedHistory = [...history].sort((a, b) => 
        (a.ratingUpdateTimeSeconds || 0) - (b.ratingUpdateTimeSeconds || 0)
    );
    
    const ratings = sortedHistory.map(h => h.newRating).filter(r => r !== null && r !== undefined);
    if (ratings.length === 0) {
        return '<div style="color: #666; font-size: 13px;">No rating history available</div>';
    }
    
    const minRating = Math.min(...ratings);
    const maxRating = Math.max(...ratings);
    const range = maxRating - minRating || 100;
    
    const width = 300;
    const height = 100;
    const padding = 10;
    
    const points = ratings.map((rating, idx) => {
        const x = padding + (idx / (ratings.length - 1)) * (width - 2 * padding);
        const y = height - padding - ((rating - minRating) / range) * (height - 2 * padding);
        return `${x},${y}`;
    }).join(' ');
    
    return `
        <svg class="rating-chart" viewBox="0 0 ${width} ${height}" preserveAspectRatio="none">
            <polyline 
                points="${points}" 
                fill="none" 
                stroke="#666" 
                stroke-width="1.5"
            />
        </svg>
    `;
}

function renderProfiles() {
    const loadingState = document.getElementById('loadingState');
    const emptyState = document.getElementById('emptyState');
    const errorState = document.getElementById('errorState');
    const profilesList = document.getElementById('profilesList');
    
    loadingState.style.display = 'none';
    errorState.style.display = 'none';
    
    if (profiles.length === 0) {
        emptyState.style.display = 'block';
        profilesList.innerHTML = '';
        return;
    }
    
    emptyState.style.display = 'none';
    profilesList.innerHTML = profiles.map(profile => `
        <div class="profile-card">
            <div class="profile-header">
                <div class="profile-info">
                    <h3>${escapeHtml(profile.handle)}</h3>
                    <div class="profile-meta">
                        ${profile.rank ? escapeHtml(profile.rank) : 'Unrated'}
                        ${profile.country ? ' • ' + escapeHtml(profile.country) : ''}
                        ${profile.organization ? ' • ' + escapeHtml(profile.organization) : ''}
                    </div>
                </div>
                <button class="btn btn-secondary" onclick="refreshProfile('${escapeHtml(profile.handle)}')">Refresh</button>
            </div>
            
            <div class="profile-stats">
                <div class="stat">
                    <span class="stat-label">Current Rating</span>
                    <span class="stat-value">${profile.rating !== null && profile.rating !== undefined ? profile.rating : 'N/A'}</span>
                </div>
                <div class="stat">
                    <span class="stat-label">Peak Rating</span>
                    <span class="stat-value">${profile.maxRating !== null && profile.maxRating !== undefined ? profile.maxRating : 'N/A'}</span>
                </div>
                <div class="stat">
                    <span class="stat-label">Max Rank</span>
                    <span class="stat-value">${profile.maxRank ? escapeHtml(profile.maxRank) : 'N/A'}</span>
                </div>
            </div>
            
            <div class="sync-info">
                Last synchronized: ${formatDate(profile.lastSyncedAt)}
            </div>
            
            <div class="chart-container">
                <div class="chart-title">Rating History</div>
                ${renderRatingChart(profile.ratingHistory)}
            </div>
        </div>
    `).join('');
}

function escapeHtml(text) {
    if (text === null || text === undefined) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

async function loadProfiles() {
    const loadingState = document.getElementById('loadingState');
    const errorState = document.getElementById('errorState');
    
    loadingState.style.display = 'block';
    errorState.style.display = 'none';
    
    try {
        profiles = await CodeforcesAPI.listProfiles() || [];
        renderProfiles();
    } catch (error) {
        loadingState.style.display = 'none';
        errorState.textContent = 'Failed to load profiles. Please try again.';
        errorState.style.display = 'block';
        console.error('Error loading profiles:', error);
    }
}

async function addProfile() {
    const handleInput = document.getElementById('handleInput');
    const addBtn = document.getElementById('addProfileBtn');
    const addMessage = document.getElementById('addMessage');
    
    const handle = handleInput.value.trim();
    if (!handle) {
        addMessage.textContent = 'Please enter a username';
        addMessage.style.color = '#d33';
        return;
    }
    
    addBtn.disabled = true;
    addBtn.textContent = 'Adding...';
    addMessage.textContent = '';
    
    try {
        await CodeforcesAPI.addProfile(handle);
        handleInput.value = '';
        addMessage.textContent = 'Profile added successfully';
        addMessage.style.color = '#2d5';
        await loadProfiles();
    } catch (error) {
        addMessage.textContent = 'Failed to add profile. Check the username and try again.';
        addMessage.style.color = '#d33';
        console.error('Error adding profile:', error);
    } finally {
        addBtn.disabled = false;
        addBtn.textContent = 'Add Profile';
    }
}

async function refreshProfile(handle) {
    try {
        await CodeforcesAPI.refreshProfile(handle);
        await loadProfiles();
    } catch (error) {
        alert('Failed to refresh profile');
        console.error('Error refreshing profile:', error);
    }
}

async function refreshAll() {
    const refreshBtn = document.getElementById('refreshAllBtn');
    refreshBtn.disabled = true;
    refreshBtn.textContent = 'Refreshing...';
    
    try {
        for (const profile of profiles) {
            await CodeforcesAPI.refreshProfile(profile.handle);
        }
        await loadProfiles();
    } catch (error) {
        console.error('Error refreshing all profiles:', error);
    } finally {
        refreshBtn.disabled = false;
        refreshBtn.textContent = 'Refresh';
    }
}

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('addProfileBtn').addEventListener('click', addProfile);
    
    document.getElementById('handleInput').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            addProfile();
        }
    });
    
    document.getElementById('refreshAllBtn').addEventListener('click', refreshAll);
    
    loadProfiles();
});
