const BACKEND_URL = 'http://' + window.location.hostname + ':8000'

function post(url, body_json) {
    if (url.startsWith('/')) {
        url = url.substring(1)
    }
    if (! url.endsWith('/')) {
        url = url + '/'
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

function post_jwt(url, body_json) {
    if (url.startsWith('/')) {
        url = url.substring(1)
    }
    if (! url.endsWith('/')) {
        url = url + '/'
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

function put(url, body_json) {
    if (url.startsWith('/')) {
        url = url.substring(1)
    }
    if (! url.endsWith('/')) {
        url = url + '/'
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

function put_jwt(url, body_json) {
    if (url.startsWith('/')) {
        url = url.substring(1)
    }
    if (! url.endsWith('/')) {
        url = url + '/'
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

function delete_(url, body_json) {
    if (url.startsWith('/')) {
        url = url.substring(1)
    }
    if (! url.endsWith('/')) {
        url = url + '/'
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

function delete_jwt(url, body_json) {
    if (url.startsWith('/')) {
        url = url.substring(1)
    }
    if (! url.endsWith('/')) {
        url = url + '/'
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

function get(url, params_json) {
    if (url.startsWith('/')) {
        url = url.substring(1)
    }
    if (! url.endsWith('/')) {
        url = url + '/'
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

function get_jwt(url, params_json) {
    if (url.startsWith('/')) {
        url = url.substring(1)
    }
    if (! url.endsWith('/')) {
        url = url + '/'
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