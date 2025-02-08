/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import React, { useState, useEffect } from 'react';
import axios from 'axios';

function App() {
    const [items, setItems] = useState([]);
    const [newItem, setNewItem] = useState('');
    const [updatedItem, setUpdatedItem] = useState({ id: '', name: '' });

    // Fetch items from the Express API
    useEffect(() => {
        axios.get('http://localhost:3000/items')
            .then(response => {
                setItems(response.data);
            })
            .catch(error => {
                console.error("There was an error fetching the items:", error);
            });
    }, []);

    // Add new item to the list
    const handleAddItem = () => {
        axios.post('http://localhost:3000/items', { name: newItem })
            .then(response => {
                setItems([...items, response.data]);
                setNewItem('');
            })
            .catch(error => {
                console.error("There was an error adding the item:", error);
            });
    };

    // Update an existing item
    const handleUpdateItem = () => {
        axios.put(`http://localhost:3000/items/${updatedItem.id}`, { name: updatedItem.name })
            .then(response => {
                const updatedItems = items.map(item =>
                    item.id === updatedItem.id ? response.data : item
                );
                setItems(updatedItems);
                setUpdatedItem({ id: '', name: '' });
            })
            .catch(error => {
                console.error("There was an error updating the item:", error);
            });
    };

    // Delete an item
    const handleDeleteItem = (id) => {
        axios.delete(`http://localhost:3000/items/${id}`)
            .then(() => {
                setItems(items.filter(item => item.id !== id));
            })
            .catch(error => {
                console.error("There was an error deleting the item:", error);
            });
    };

    return (
        <div className="App">
            <h1>Items List</h1>

            {/* Display List of Items */}
            <ul>
                {items.map(item => (
                    <li key={item.id}>
                        {item.name}
                        <button onClick={() => handleDeleteItem(item.id)}>Delete</button>
                    </li>
                ))}
            </ul>

            {/* Add New Item */}
            <input
                type="text"
                value={newItem}
                onChange={(e) => setNewItem(e.target.value)}
                placeholder="Add new item"
            />
            <button onClick={handleAddItem}>Add Item</button>

            {/* Update Existing Item */}
            <div>
                <input
                    type="text"
                    value={updatedItem.id}
                    onChange={(e) => setUpdatedItem({ ...updatedItem, id: e.target.value })}
                    placeholder="Item ID to update"
                />
                <input
                    type="text"
                    value={updatedItem.name}
                    onChange={(e) => setUpdatedItem({ ...updatedItem, name: e.target.value })}
                    placeholder="New item name"
                />
                <button onClick={handleUpdateItem}>Update Item</button>
            </div>
        </div>
    );
}

export default App;
