const API_BASE = 'http://localhost:8080/counterproject/api';

const api = {
    // Queues
    async getQueues() {
        const response = await fetch(`${API_BASE}/queues`);
        return response.json();
    },

    async getQueue(id) {
        const response = await fetch(`${API_BASE}/queues/${id}`);
        return response.json();
    },

    async createQueue(queue) {
        const response = await fetch(`${API_BASE}/queues`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(queue)
        });
        return response.json();
    },

    async updateQueue(id, queue) {
        const response = await fetch(`${API_BASE}/queues/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(queue)
        });
        return response.json();
    },

    async deleteQueue(id) {
        await fetch(`${API_BASE}/queues/${id}`, { method: 'DELETE' });
    },

    async clearAllDiffs(id) {
        await fetch(`${API_BASE}/queues/${id}/clear-diffs`, { method: 'POST' });
    },

    async linkTemplate(queueId, request) {
        await fetch(`${API_BASE}/queues/${queueId}/link-template`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(request)
        });
    },

    async getMetrics(queueId, dtRif) {
        const url = dtRif
            ? `${API_BASE}/queues/${queueId}/metrics?dtRif=${dtRif}`
            : `${API_BASE}/queues/${queueId}/metrics`;
        const response = await fetch(url);
        return response.json();
    },

    // Diffs
    async getDiffs(queueId) {
        const response = await fetch(`${API_BASE}/diffs?queueId=${queueId}`);
        return response.json();
    },

    async getDiff(id) {
        const response = await fetch(`${API_BASE}/diffs/${id}`);
        return response.json();
    },

    async createDiff(diff) {
        const response = await fetch(`${API_BASE}/diffs`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(diff)
        });
        return response.json();
    },

    async updateDiff(id, diff) {
        const response = await fetch(`${API_BASE}/diffs/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(diff)
        });
        return response.json();
    },

    async deleteDiff(id) {
        await fetch(`${API_BASE}/diffs/${id}`, { method: 'DELETE' });
    },

    // Templates
    async getTemplates() {
        const response = await fetch(`${API_BASE}/templates`);
        return response.json();
    },

    async getTemplate(id) {
        const response = await fetch(`${API_BASE}/templates/${id}`);
        return response.json();
    },

    async createTemplate(template) {
        const response = await fetch(`${API_BASE}/templates`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(template)
        });
        return response.json();
    },

    async updateTemplate(id, template) {
        const response = await fetch(`${API_BASE}/templates/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(template)
        });
        return response.json();
    },

    async deleteTemplate(id) {
        await fetch(`${API_BASE}/templates/${id}`, { method: 'DELETE' });
    }
};
