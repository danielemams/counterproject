let queueId = null;
let currentQueue = null;
let diffs = [];
let templates = [];

async function init() {
    const urlParams = new URLSearchParams(window.location.search);
    queueId = urlParams.get('id');

    if (!queueId) {
        showError('No queue ID provided');
        return;
    }

    // Set dtRif to today
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('dtRif').value = today;

    await loadQueue();
    await loadDiffs();
    await loadMetrics();
    await loadTemplates();
}

async function loadQueue() {
    try {
        currentQueue = await api.getQueue(queueId);
        document.getElementById('queueTitle').textContent = `Queue: ${currentQueue.name}`;
    } catch (error) {
        showError('Failed to load queue: ' + error.message);
    }
}

async function loadDiffs() {
    try {
        diffs = await api.getDiffs(queueId);
        renderDiffs();
    } catch (error) {
        showError('Failed to load diffs: ' + error.message);
    }
}

async function loadMetrics() {
    try {
        const dtRif = document.getElementById('dtRif').value;
        const metrics = await api.getMetrics(queueId, dtRif);
        renderMetrics(metrics);
    } catch (error) {
        showError('Failed to load metrics: ' + error.message);
    }
}

async function loadTemplates() {
    try {
        templates = await api.getTemplates();
    } catch (error) {
        showError('Failed to load templates: ' + error.message);
    }
}

function renderDiffs() {
    const tbody = document.getElementById('diffsBody');
    tbody.innerHTML = '';

    diffs.forEach(diff => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${diff.id}</td>
            <td class="${diff.value < 0 ? 'metric-value negative' : 'metric-value'}">${diff.value}</td>
            <td>${formatDateTime(diff.dtDiff)}</td>
            <td>${diff.dtExpiry || '-'}</td>
            <td>${diff.isManual !== null ? (diff.isManual ? 'Yes' : 'No') : '-'}</td>
            <td>${diff.description || '-'}</td>
            <td>${diff.templateId ? 'Yes (ID: ' + diff.templateId + ')' : 'No'}</td>
            <td>
                <button class="btn btn-secondary" onclick="editDiff(${diff.id})">Edit</button>
                <button class="btn btn-danger" onclick="deleteDiff(${diff.id})">Delete</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function renderMetrics(metrics) {
    const content = document.getElementById('metricsContent');
    content.innerHTML = `
        <div class="metric-item">
            <span class="metric-label">Reference Date:</span>
            <span>${metrics.dtRif}</span>
        </div>
        <div class="metric-item">
            <span class="metric-label">Current Value:</span>
            <span class="${metrics.currentValue < 0 ? 'metric-value negative' : 'metric-value'}">${formatCurrency(metrics.currentValue)}</span>
        </div>
        <div class="metric-item">
            <span class="metric-label">Expected Value:</span>
            <span class="${metrics.expectedValue < 0 ? 'metric-value negative' : 'metric-value'}">${formatCurrency(metrics.expectedValue)}</span>
        </div>
        <div class="metric-item">
            <span class="metric-label">Budget Current:</span>
            <span class="metric-value">${formatCurrency(metrics.budgetCurrent)} ${metrics.hasDtMaxExpiry ? 'per day' : '(NA/Infinite)'}</span>
        </div>
        <div class="metric-item">
            <span class="metric-label">Spese alla dtRif EOD:</span>
            <span class="metric-value negative">${formatCurrency(metrics.speseAllaDtRifEOD)}</span>
        </div>
        <div class="metric-item">
            <span class="metric-label">Spese Expected alla dtRif EOD:</span>
            <span class="metric-value negative">${formatCurrency(metrics.spesaExpectedAllaDtRifEOD)}</span>
        </div>
        ${metrics.hasDtMaxExpiry ? `
        <div class="metric-item">
            <span class="metric-label">Max Expiry Date:</span>
            <span>${metrics.dtMaxExpiry}</span>
        </div>
        ` : ''}
    `;
}

function adjustDate(days) {
    const input = document.getElementById('dtRif');
    const currentDate = new Date(input.value);
    currentDate.setDate(currentDate.getDate() + days);
    input.value = currentDate.toISOString().split('T')[0];
    loadMetrics();
}

function showAddDiffModal() {
    document.getElementById('diffModalTitle').textContent = 'Add Diff';
    document.getElementById('diffForm').reset();
    document.getElementById('diffId').value = '';
    document.getElementById('diffModal').style.display = 'block';
}

function closeDiffModal() {
    document.getElementById('diffModal').style.display = 'none';
}

async function editDiff(id) {
    try {
        const diff = await api.getDiff(id);
        document.getElementById('diffModalTitle').textContent = 'Edit Diff';
        document.getElementById('diffId').value = diff.id;
        document.getElementById('diffValue').value = diff.value;
        document.getElementById('diffDtDiff').value = formatDateTimeForInput(diff.dtDiff);
        document.getElementById('diffDtExpiry').value = diff.dtExpiry || '';
        document.getElementById('diffIsManual').checked = diff.isManual || false;
        document.getElementById('diffDescription').value = diff.description || '';
        updateDiffFormFields();
        document.getElementById('diffModal').style.display = 'block';
    } catch (error) {
        showError('Failed to load diff: ' + error.message);
    }
}

function updateDiffFormFields() {
    const value = parseFloat(document.getElementById('diffValue').value);
    const expiryGroup = document.getElementById('diffExpiryGroup');
    const isManualGroup = document.getElementById('diffIsManualGroup');

    if (value < 0) {
        expiryGroup.style.display = 'none';
        document.getElementById('diffDtExpiry').value = '';
        isManualGroup.style.display = 'block';
    } else if (value > 0) {
        expiryGroup.style.display = 'block';
        isManualGroup.style.display = 'none';
        document.getElementById('diffIsManual').checked = false;
    }
}

async function saveDiff(event) {
    event.preventDefault();

    const id = document.getElementById('diffId').value;
    const value = parseFloat(document.getElementById('diffValue').value);

    const diff = {
        queueId: parseInt(queueId),
        value: value,
        dtDiff: document.getElementById('diffDtDiff').value,
        dtExpiry: value > 0 ? (document.getElementById('diffDtExpiry').value || null) : null,
        isManual: value < 0 ? document.getElementById('diffIsManual').checked : null,
        description: document.getElementById('diffDescription').value || null
    };

    try {
        if (id) {
            await api.updateDiff(id, diff);
            showSuccess('Diff updated successfully');
        } else {
            await api.createDiff(diff);
            showSuccess('Diff created successfully');
        }
        closeDiffModal();
        await loadDiffs();
        await loadMetrics();
    } catch (error) {
        showError('Failed to save diff: ' + error.message);
    }
}

async function deleteDiff(id) {
    if (!confirm('Are you sure you want to delete this diff?')) {
        return;
    }

    try {
        await api.deleteDiff(id);
        showSuccess('Diff deleted successfully');
        await loadDiffs();
        await loadMetrics();
    } catch (error) {
        showError('Failed to delete diff: ' + error.message);
    }
}

function editQueue() {
    document.getElementById('queueName').value = currentQueue.name;
    document.getElementById('queueInitValue').value = currentQueue.initValue;
    document.getElementById('queueDtInitValue').value = formatDateTimeForInput(currentQueue.dtInitValue);
    document.getElementById('queueDtExpiryInitValue').value = currentQueue.dtExpiryInitValue || '';
    document.getElementById('queueIsWithLinearConsumption').checked = currentQueue.withLinearConsumption;
    document.getElementById('queueModal').style.display = 'block';
}

function closeQueueModal() {
    document.getElementById('queueModal').style.display = 'none';
}

async function saveQueue(event) {
    event.preventDefault();

    const queue = {
        name: document.getElementById('queueName').value,
        initValue: parseFloat(document.getElementById('queueInitValue').value),
        dtInitValue: document.getElementById('queueDtInitValue').value,
        dtExpiryInitValue: document.getElementById('queueDtExpiryInitValue').value || null,
        withLinearConsumption: document.getElementById('queueIsWithLinearConsumption').checked
    };

    try {
        await api.updateQueue(queueId, queue);
        showSuccess('Queue updated successfully');
        closeQueueModal();
        await loadQueue();
        await loadMetrics();
    } catch (error) {
        showError('Failed to save queue: ' + error.message);
    }
}

async function deleteQueue() {
    if (!confirm('Are you sure you want to delete this queue? All diffs will be deleted.')) {
        return;
    }

    try {
        await api.deleteQueue(queueId);
        showSuccess('Queue deleted successfully');
        setTimeout(() => window.location.href = 'index.html', 1000);
    } catch (error) {
        showError('Failed to delete queue: ' + error.message);
    }
}

async function clearAllDiffs() {
    if (!confirm('Are you sure you want to clear all diffs from this queue?')) {
        return;
    }

    try {
        await api.clearAllDiffs(queueId);
        showSuccess('All diffs cleared successfully');
        await loadDiffs();
        await loadMetrics();
    } catch (error) {
        showError('Failed to clear diffs: ' + error.message);
    }
}

function showLinkTemplateModal() {
    const select = document.getElementById('linkTemplateId');
    select.innerHTML = '<option value="">Select a template...</option>';
    templates.forEach(t => {
        const option = document.createElement('option');
        option.value = t.id;
        option.textContent = `${t.name} (${t.value}, ${t.frequencyUnit})`;
        select.appendChild(option);
    });

    document.getElementById('linkTemplateForm').reset();
    document.getElementById('linkTemplateModal').style.display = 'block';
}

function closeLinkTemplateModal() {
    document.getElementById('linkTemplateModal').style.display = 'none';
}

async function saveLinkTemplate(event) {
    event.preventDefault();

    const request = {
        templateId: parseInt(document.getElementById('linkTemplateId').value),
        dtStartLink: document.getElementById('linkDtStartLink').value,
        dtEndLink: document.getElementById('linkDtEndLink').value
    };

    try {
        await api.linkTemplate(queueId, request);
        showSuccess('Template linked successfully');
        closeLinkTemplateModal();
        await loadDiffs();
        await loadMetrics();
    } catch (error) {
        showError('Failed to link template: ' + error.message);
    }
}

function goBack() {
    window.location.href = 'index.html';
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

function formatCurrency(value) {
    return parseFloat(value).toFixed(2);
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

// Initialize on page load
init();
