const API_BASE = '';

async function apiRequest(url, options = {}) {
    try {
        const response = await fetch(url, {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return await response.json();
        }
        return null;
    } catch (error) {
        console.error('API request failed:', error);
        throw error;
    }
}

const CodeforcesAPI = {
    addProfile: (handle) => {
        return apiRequest('/api/codeforces/profiles', {
            method: 'POST',
            body: JSON.stringify({ handle: handle })
        });
    },
    
    listProfiles: () => {
        return apiRequest('/api/codeforces/profiles');
    },
    
    refreshProfile: (handle) => {
        return apiRequest(`/api/codeforces/profiles/${encodeURIComponent(handle)}/refresh`, {
            method: 'POST'
        });
    }
};
