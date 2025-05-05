<template>
  <div class="app-layout">
    <!-- Left Panel: Form -->
    <div class="left-panel">
      <h2>Create Record</h2>
      <form @submit.prevent="submitForm">
        <label>Address: <input v-model="form.address" required /></label>
        <label>Acreage: <input type="number" v-model="form.acreage" step="0.01" required /></label>
        <label>Owner First Name: <input v-model="form.ownerFirstName" required /></label>
        <label>Owner Last Name: <input v-model="form.ownerLastName" required /></label>
        <label>Acquisition Type: <input v-model="form.acquisitionType" /></label>
        <label>Last Sold Price: <input type="number" v-model="form.lastSoldPrice" step="0.01" /></label>
        <button type="submit">Create Record & Generate PDF</button>
      </form>
    </div>

    <!-- Center Panel: Drop Zone -->
    <div class="center-panel">
      <h2>Upload PDF</h2>
      <PdfDropZone />
    </div>

    <!-- Right Panel: CRUD/Search -->
    <div class="right-panel">
      <h2>Manage Records</h2>

      <label>
        Search by Property ID or Owner Name:
        <input v-model="searchQuery" placeholder="Input id or name" />
      </label>
      <button @click="searchRecord">Search</button>

      <div v-if="searchResult && !isEditing">
        <h3>Search Result</h3>
        <p><strong>Property ID:</strong> {{ searchResult.propertyId }}</p>
        <p><strong>Address:</strong> {{ searchResult.address }}</p>
        <p><strong>Acreage:</strong> {{ searchResult.acreage }}</p>
        <p><strong>Owner:</strong> {{ searchResult.owner.firstName }} {{ searchResult.owner.lastName }}</p>
        <p><strong>Acquisition Type:</strong> {{ searchResult.acquisitionType }}</p>
        <p><strong>Last Sold Price:</strong> ${{ searchResult.lastSoldPrice }}</p>

        <button @click="deleteRecord">Delete</button>
        <button @click="enableUpdate">Update</button>
      </div>

      <div v-if="isEditing">
        <h3>Update Property</h3>
        <form @submit.prevent="submitUpdate">
          <label>Address: <input v-model="editForm.address" required /></label>
          <label>Acreage: <input type="number" v-model="editForm.acreage" step="0.01" required /></label>
          <label>Owner First Name: <input v-model="editForm.ownerFirstName" required /></label>
          <label>Owner Last Name: <input v-model="editForm.ownerLastName" required /></label>
          <label>Acquisition Type: <input v-model="editForm.acquisitionType" /></label>
          <label>Last Sold Price: <input type="number" v-model="editForm.lastSoldPrice" step="0.01" /></label>
          <button type="submit">Save Update</button>
          <button type="button" @click="cancelUpdate">Cancel</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import PdfDropZone from './components/PdfDropZone.vue'
import { reactive, ref } from 'vue'

const form = reactive({
  address: '',
  acreage: '',
  ownerFirstName: '',
  ownerLastName: '',
  acquisitionType: '',
  lastSoldPrice: ''
})

const searchQuery = ref('')
const searchResult = ref(null)
const isEditing = ref(false)

const editForm = reactive({
  address: '',
  acreage: 0,
  ownerFirstName: '',
  ownerLastName: '',
  acquisitionType: '',
  lastSoldPrice: 0
})

function submitForm() {
  const payload = {
    address: form.address,
    acreage: parseFloat(form.acreage),
    acquisitionType: form.acquisitionType,
    lastSoldPrice: parseFloat(form.lastSoldPrice),
    owner: {
      firstName: form.ownerFirstName,
      lastName: form.ownerLastName
    }
  }

  fetch('http://localhost:8080/api/properties', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  })
    .then(response => {
      if (!response.ok) throw new Error('Failed to create record')
      return response.json()
    })
    .then(() => {
      alert('Record saved successfully!')
    })
    .catch(error => {
      console.error(error)
      alert('Error saving record.')
    })
}

function searchRecord() {
  if (!searchQuery.value) {
    alert("Please enter a Property ID or Owner's name.")
    return
  }

  if (!isNaN(searchQuery.value)) {
    const id = parseInt(searchQuery.value)
    fetch(`http://localhost:8080/api/properties/${id}`)
      .then(response => {
        if (!response.ok) throw new Error("Not found")
        return response.json()
      })
      .then(data => {
        searchResult.value = data
        isEditing.value = false
      })
      .catch(() => {
        alert("No property found with that ID.")
        searchResult.value = null
      })
  } else {
    alert("Name-based search not implemented yet. Use a Property ID for now.")
  }
}

function deleteRecord() {
  if (!searchResult.value || !searchResult.value.propertyId) return

  const confirmDelete = confirm(`Delete property ID ${searchResult.value.propertyId}?`)
  if (!confirmDelete) return

  fetch(`http://localhost:8080/api/properties/${searchResult.value.propertyId}`, {
    method: 'DELETE'
  })
    .then(response => {
      if (!response.ok) throw new Error("Delete failed")
      alert("Property deleted.")
      searchResult.value = null
      searchQuery.value = ''
    })
    .catch(err => {
      console.error(err)
      alert("Error deleting record.")
    })
}

function enableUpdate() {
  if (!searchResult.value) return

  editForm.address = searchResult.value.address
  editForm.acreage = searchResult.value.acreage
  editForm.ownerFirstName = searchResult.value.owner.firstName
  editForm.ownerLastName = searchResult.value.owner.lastName
  editForm.acquisitionType = searchResult.value.acquisitionType
  editForm.lastSoldPrice = searchResult.value.lastSoldPrice

  isEditing.value = true
}

function cancelUpdate() {
  isEditing.value = false
}

function submitUpdate() {
  const id = searchResult.value.propertyId
  const payload = {
    address: editForm.address,
    acreage: parseFloat(editForm.acreage),
    acquisitionType: editForm.acquisitionType,
    lastSoldPrice: parseFloat(editForm.lastSoldPrice),
    owner: {
      firstName: editForm.ownerFirstName,
      lastName: editForm.ownerLastName
    }
  }

  fetch(`http://localhost:8080/api/properties/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  })
    .then(response => {
      if (!response.ok) throw new Error("Update failed")
      return response.json()
    })
    .then(data => {
      alert("Property updated successfully.")
      searchResult.value = data
      isEditing.value = false
    })
    .catch(err => {
      console.error(err)
      alert("Error updating record.")
    })
}
</script>

<style>
.app-layout {
  display: flex;
  height: 100vh;
}
.left-panel,
.center-panel,
.right-panel {
  flex: 1;
  padding: 1rem;
  border-right: 1px solid #ccc;
  box-sizing: border-box;
}
.right-panel {
  border-right: none;
}
</style>
