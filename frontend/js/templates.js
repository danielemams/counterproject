let templates = [];

async function loadTemplates() {
    try {
        templates = await api.getTemplates();
        renderTemplates();
    } catch (error) {
        showError('Failed to load templates: ' + error.message);
    }
}

function renderTemplates() {
    const tbody = document.getElementById('templatesBody');
    tbody.innerHTML = '';

    templates.forEach(template => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${template.id}</td>
            <td>${template.name}</td>
            <td>${template.value}</td>
            <td>${template.dayOfPeriod}</td>
            <td>${template.frequencyNum} ${template.frequencyUnit}</td>
            <td>
                <button class="btn btn-secondary" onclick="editTemplate(${template.id})">Edit</button>
                <button class="btn btn-danger" onclick="deleteTemplate(${template.id})">Delete</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function showCreateTemplateModal() {
    document.getElementById('modalTitle').textContent = 'Create Template';
    document.getElementById('templateForm').reset();
    document.getElementById('templateId').value = '';
    document.getElementById('templateModal').style.display = 'block';
    updateFrequencyFields();
}

function closeTemplateModal() {
    document.getElementById('templateModal').style.display = 'none';
}

async function editTemplate(id) {
    try {
        const template = await api.getTemplate(id);
        document.getElementById('modalTitle').textContent = 'Edit Template';
        document.getElementById('templateId').value = template.id;
        document.getElementById('templateName').value = template.name;
        document.getElementById('templateValue').value = template.value;
        document.getElementById('templateFrequencyUnit').value = template.frequencyUnit;
        document.getElementById('templateFrequencyNum').value = template.frequencyNum;
        document.getElementById('templateDayOfPeriod').value = template.dayOfPeriod;
        updateFrequencyFields();
        document.getElementById('templateModal').style.display = 'block';
    } catch (error) {
        showError('Failed to load template: ' + error.message);
    }
}

function updateFrequencyFields() {
    const unit = document.getElementById('templateFrequencyUnit').value;
    const frequencyNumGroup = document.getElementById('frequencyNumGroup');
    const dayOfPeriodInput = document.getElementById('templateDayOfPeriod');
    const dayOfPeriodLabel = document.getElementById('dayOfPeriodLabel');
    const dayOfPeriodHelp = document.getElementById('dayOfPeriodHelp');

    // For DAYS, frequencyNum is always visible but we can hide it if desired
    // Based on requirements, for DAYS we don't really need to show frequencyNum
    if (unit === 'DAYS') {
        frequencyNumGroup.style.display = 'block'; // Keep visible but could hide
        dayOfPeriodLabel.textContent = 'Day of Period (not used for DAYS):';
        dayOfPeriodHelp.textContent = 'Not applicable for daily frequency';
        dayOfPeriodInput.value = 1;
        dayOfPeriodInput.max = 1;
    } else if (unit === 'WEEKS') {
        frequencyNumGroup.style.display = 'block';
        dayOfPeriodLabel.textContent = 'Day of Week (1=Monday, 7=Sunday):';
        dayOfPeriodHelp.textContent = '1-7 (Monday to Sunday)';
        dayOfPeriodInput.max = 7;
    } else if (unit === 'MONTHS') {
        frequencyNumGroup.style.display = 'block';
        dayOfPeriodLabel.textContent = 'Day of Month:';
        dayOfPeriodHelp.textContent = '1-31';
        dayOfPeriodInput.max = 31;
    } else if (unit === 'YEARS') {
        frequencyNumGroup.style.display = 'block';
        dayOfPeriodLabel.textContent = 'Day of Year:';
        dayOfPeriodHelp.textContent = '1-366';
        dayOfPeriodInput.max = 366;
    } else {
        frequencyNumGroup.style.display = 'block';
        dayOfPeriodLabel.textContent = 'Day of Period:';
        dayOfPeriodHelp.textContent = '';
    }
}

async function saveTemplate(event) {
    event.preventDefault();

    const id = document.getElementById('templateId').value;
    const template = {
        name: document.getElementById('templateName').value,
        value: parseFloat(document.getElementById('templateValue').value),
        frequencyUnit: document.getElementById('templateFrequencyUnit').value,
        frequencyNum: parseInt(document.getElementById('templateFrequencyNum').value),
        dayOfPeriod: parseInt(document.getElementById('templateDayOfPeriod').value)
    };

    try {
        if (id) {
            await api.updateTemplate(id, template);
            showSuccess('Template updated successfully');
        } else {
            await api.createTemplate(template);
            showSuccess('Template created successfully');
        }
        closeTemplateModal();
        loadTemplates();
    } catch (error) {
        showError('Failed to save template: ' + error.message);
    }
}

async function deleteTemplate(id) {
    if (!confirm('Are you sure you want to delete this template? Links will be removed but generated diffs will remain.')) {
        return;
    }

    try {
        await api.deleteTemplate(id);
        showSuccess('Template deleted successfully');
        loadTemplates();
    } catch (error) {
        showError('Failed to delete template: ' + error.message);
    }
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

// Load templates on page load
loadTemplates();
