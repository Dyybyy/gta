let inventory = {
    food: {},
    supplies: {}
};

let history = [];

// Function to generate random inventory data
function generateRandomInventory() {
    const foodItems = ['Tomatoes', 'Lettuce', 'Chicken', 'Beef', 'Rice', 'Pasta', 'Cheese', 'Onions'];
    const supplyItems = ['Plates', 'Forks', 'Knives', 'Spoons', 'Napkins', 'To-go boxes', 'Cleaning supplies'];

    foodItems.forEach(item => {
        inventory.food[item] = Math.floor(Math.random() * 100) + 1; // Random quantity between 1 and 100
    });

    supplyItems.forEach(item => {
        inventory.supplies[item] = Math.floor(Math.random() * 200) + 1; // Random quantity between 1 and 200
    });

    updateTables();
    populateDatalist();
    saveToLocalStorage();
}

// Populate datalist with items
function populateDatalist() {
    const datalist = document.getElementById('items');
    datalist.innerHTML = '';  // Clear previous options

    // Add food items to datalist
    Object.keys(inventory.food).forEach(item => {
        const option = document.createElement('option');
        option.value = item;
        datalist.appendChild(option);
    });

    // Add supply items to datalist
    Object.keys(inventory.supplies).forEach(item => {
        const option = document.createElement('option');
        option.value = item;
        datalist.appendChild(option);
    });
}

// Update food and supplies tables
function updateTables(foodFilter = '', suppliesFilter = '') {
    const foodTable = document.getElementById('foodTable');
    const suppliesTable = document.getElementById('suppliesTable');

    // Clear current table data
    foodTable.innerHTML = '';
    suppliesTable.innerHTML = '';

    // Populate food items
    for (let item in inventory.food) {
        if (item.toLowerCase().includes(foodFilter.toLowerCase())) {
            const row = createRow(item, inventory.food[item], 'food');
            foodTable.appendChild(row);
        }
    }

    // Populate supplies items
    for (let item in inventory.supplies) {
        if (item.toLowerCase().includes(suppliesFilter.toLowerCase())) {
            const row = createRow(item, inventory.supplies[item], 'supplies');
            suppliesTable.appendChild(row);
        }
    }
}

// Create row for the inventory table
function createRow(item, quantity, category) {
    const row = document.createElement('tr');
    let colorClass = '';
    
    if (quantity <= 5) {
        colorClass = 'low-stock';
    } else if (quantity <= 10) {
        colorClass = 'medium-stock';
    } else {
        colorClass = 'high-stock';
    }
    
    row.className = colorClass;
    row.innerHTML = `
        <td>${item}</td>
        <td>${quantity}</td>
        <td>
            <button onclick="editItem('${item}', '${category}')">Edit</button>
        </td>
    `;
    return row;
}

// Edit an existing item
function editItem(item, category) {
    const newName = prompt('Enter the new name for the item:', item);
    const newQuantity = prompt('Enter the new quantity:', inventory[category][item]);

    if (newName && newQuantity) {
        // Remove the old entry and add the updated one
        delete inventory[category][item];
        inventory[category][newName] = parseFloat(newQuantity);

        saveToLocalStorage();
        updateTables();
        populateDatalist();
    }
}

// Add action to the history log
function addHistoryLog(item, action, quantity) {
    const log = {
        date: new Date().toLocaleDateString(),
        item,
        action,
        quantity
    };
    history.push(log);
    updateHistoryTable();
    saveToLocalStorage();
}

// Update the history table with filtering options
function updateHistoryTable(days = 0) {
    const historyTable = document.getElementById('historyTable');
    historyTable.innerHTML = '';

    const today = new Date();
    const filteredHistory = history.filter(log => {
        const logDate = new Date(log.date);
        const diffDays = Math.floor((today - logDate) / (1000 * 60 * 60 * 24));
        return days === 0 || diffDays <= days;
    });

    filteredHistory.forEach(log => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${log.date}</td>
            <td>${log.item}</td>
            <td>${log.action}</td>
            <td>${log.quantity}</td>
        `;
        historyTable.appendChild(row);
    });
}

// Create date filter buttons
function createDateFilterButtons() {
    const dateButtons = document.getElementById('dateButtons');
    dateButtons.innerHTML = `
        <button onclick="updateHistoryTable(0)">Today</button>
        <button onclick="updateHistoryTable(7)">1 Week</button>
        <button onclick="updateHistoryTable(30)">1 Month</button>
        <button onclick="updateHistoryTable(90)">3 Months</button>
        <button onclick="updateHistoryTable(Infinity)">All Time</button>
    `;
}

document.getElementById('updateForm').addEventListener('submit', function(e) {
    e.preventDefault();

    const item = document.getElementById('item').value.trim();
    const category = document.getElementById('category').value;
    const action = document.getElementById('action').value;
    const quantity = parseFloat(document.getElementById('quantity').value);

    if (!item || quantity <= 0) {
        alert('Please enter a valid item and quantity.');
        return;
    }

    // Initialize item if it doesn't exist
    if (!inventory[category][item]) {
        inventory[category][item] = 0;
    }

    // Perform add or remove action
    if (action === 'add') {
        inventory[category][item] += quantity;
    } else if (action === 'remove') {
        if (inventory[category][item] < quantity) {
            alert('Not enough stock available!');
            return;
        }
        inventory[category][item] -= quantity;
    }

    addHistoryLog(item, action, quantity);
    saveToLocalStorage();
    updateTables();
    populateDatalist();

    // Clear the form
    document.getElementById('item').value = '';
    document.getElementById('quantity').value = '';
});

// Search functionality
document.getElementById('foodSearch').addEventListener('input', function(e) {
    updateTables(e.target.value, '');
});
document.getElementById('suppliesSearch').addEventListener('input', function(e) {
    updateTables('', e.target.value);
});

// LocalStorage handling
function saveToLocalStorage() {
    localStorage.setItem('inventory', JSON.stringify(inventory));
    localStorage.setItem('history', JSON.stringify(history));
}

function loadFromLocalStorage() {
    const storedInventory = localStorage.getItem('inventory');
    const storedHistory = localStorage.getItem('history');
    if (storedInventory) {
        inventory = JSON.parse(storedInventory);
    }
    if (storedHistory) {
        history = JSON.parse(storedHistory);
    }
}

// Initial setup
loadFromLocalStorage();
generateRandomInventory();
createDateFilterButtons();
updateHistoryTable();
