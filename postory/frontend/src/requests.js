const BACKEND_URL = 'http://' + '3.125.114.231' + ':8000'

export function post(url, body_json) {
    if (url.startsWith('/')) {
        url = url.substring(1)
    }
    return fetch(`${BACKEND_URL}/${url}`, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(body_json)
    })
}

export function post_jwt(url, body_json) {
    if (url.startsWith('/')) {
        url = url.substring(1)
    }

    return fetch(`${BACKEND_URL}/${url}`, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `JWT ${localStorage.getItem('access')}`
        },
        body: JSON.stringify(body_json)
    })
}

export function put(url, body_json) {
    if (url.startsWith('/')) {
        url = url.substring(1)
    }

    return fetch(`${BACKEND_URL}/${url}`, {
        method: 'PUT',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(body_json)
    })
}

export function put_jwt(url, body_json) {
    if (url.startsWith('/')) {
        url = url.substring(1)
    }

    return fetch(`${BACKEND_URL}/${url}`, {
        method: 'PUT',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `JWT ${localStorage.getItem('access')}`
        },
        body: JSON.stringify(body_json)
    })
}

export function delete_(url, body_json) {
    if (url.startsWith('/')) {
        url = url.substring(1)
    }

    return fetch(`${BACKEND_URL}/${url}`, {
        method: 'DELETE',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(body_json)
    })
}

export function delete_jwt(url, body_json) {
    if (url.startsWith('/')) {
        url = url.substring(1)
    }

    return fetch(`${BACKEND_URL}/${url}`, {
        method: 'DELETE',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `JWT ${localStorage.getItem('access')}`
        },
        body: JSON.stringify(body_json)
    })
}

export function get(url, params_json) {
    if (url.startsWith('/')) {
        url = url.substring(1)
    }
    const params = new URLSearchParams(params_json).toString()

    return fetch(`${BACKEND_URL}/${url}${params}`, {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        }
    })
}

export function get_jwt(url, params_json) {
    if (url.startsWith('/')) {
        url = url.substring(1)
    }

    const params = new URLSearchParams(params_json).toString()

    return fetch(`${BACKEND_URL}/${url}${params}`, {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `JWT ${localStorage.getItem('access')}`
        }
    })
}

