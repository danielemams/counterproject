let queues = [];

async function loadQueues() {
    try {
        queues = await api.getQueues();
        renderQueues();
    } catch (error) {
        showError('Failed to load queues: ' + error.message);
    }
}

function renderQueues() {
    const tbody = document.getElementById('queuesBody');
    tbody.innerHTML = '';

    queues.forEach(queue => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${queue.id}</td>
            <td>${queue.name}</td>
            <td>${queue.initValue}</td>
            <td>${formatDateTime(queue.dtInitValue)}</td>
            <td>${queue.withLinearConsumption ? 'Yes' : 'No'}</td>
            <td>
                <button class="btn btn-primary" onclick="viewQueue(${queue.id})">View</button>
                <button class="btn btn-secondary" onclick="editQueue(${queue.id})">Edit</button>
                <button class="btn btn-danger" onclick="deleteQueue(${queue.id})">Delete</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function showCreateQueueModal() {
    document.getElementById('modalTitle').textContent = 'Create Queue';
    document.getElementById('queueForm').reset();
    document.getElementById('queueId').value = '';
    document.getElementById('queueModal').style.display = 'block';
}

function closeQueueModal() {
    document.getElementById('queueModal').style.display = 'none';
}

async function editQueue(id) {
    try {
        const queue = await api.getQueue(id);
        document.getElementById('modalTitle').textContent = 'Edit Queue';
        document.getElementById('queueId').value = queue.id;
        document.getElementById('queueName').value = queue.name;
        document.getElementById('queueInitValue').value = queue.initValue;
        document.getElementById('queueDtInitValue').value = formatDateTimeForInput(queue.dtInitValue);
        document.getElementById('queueDtExpiryInitValue').value = queue.dtExpiryInitValue || '';
        document.getElementById('queueIsWithLinearConsumption').checked = queue.withLinearConsumption;
        document.getElementById('queueModal').style.display = 'block';
    } catch (error) {
        showError('Failed to load queue: ' + error.message);
    }
}

async function saveQueue(event) {
    event.preventDefault();

    const id = document.getElementById('queueId').value;
    const queue = {
        name: document.getElementById('queueName').value,
        initValue: parseFloat(document.getElementById('queueInitValue').value),
        dtInitValue: document.getElementById('queueDtInitValue').value,
        dtExpiryInitValue: document.getElementById('queueDtExpiryInitValue').value || null,
        withLinearConsumption: document.getElementById('queueIsWithLinearConsumption').checked
    };

    try {
        if (id) {
            await api.updateQueue(id, queue);
            showSuccess('Queue updated successfully');
        } else {
            await api.createQueue(queue);
            showSuccess('Queue created successfully');
        }
        closeQueueModal();
        loadQueues();
    } catch (error) {
        showError('Failed to save queue: ' + error.message);
    }
}

async function deleteQueue(id) {
    if (!confirm('Are you sure you want to delete this queue?')) {
        return;
    }

    try {
        await api.deleteQueue(id);
        showSuccess('Queue deleted successfully');
        loadQueues();
    } catch (error) {
        showError('Failed to delete queue: ' + error.message);
    }
}

function viewQueue(id) {
    window.location.href = `queue-detail.html?id=${id}`;
}

function formatDateTime(dt) {
    if (!dt) return '';
    return new Date(dt).toLocaleString();
}

function formatDateTimeForInput(dt) {
    if (!dt) return '';
    const date = new Date(dt);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day}T${hours}:${minutes}`;
}

function showError(message) {
    const errorDiv = document.getElementById('error');
    errorDiv.textContent = message;
    errorDiv.style.display = 'block';
    setTimeout(() => errorDiv.style.display = 'none', 5000);
}

function showSuccess(message) {
    const successDiv = document.getElementById('success');
    successDiv.textContent = message;
    successDiv.style.display = 'block';
    setTimeout(() => successDiv.style.display = 'none', 3000);
}

// Load queues on page load
loadQueues();
