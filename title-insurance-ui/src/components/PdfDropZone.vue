<template>
    <div 
      class="drop-zone"
      @dragover.prevent
      @drop.prevent="handleDrop"
    >
      <p>Drop your PDF file here</p>
    </div>
  </template>
  
  <script setup>
  import axios from 'axios'
  
  function handleDrop(event) {
    const file = event.dataTransfer.files[0]
    if (!file || file.type !== 'application/pdf') {
      alert('Please drop a valid PDF file.')
      return
    }
  
    const formData = new FormData()
    formData.append('file', file)
  
    axios.post('http://localhost:8080/api/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    .then(() => {
      alert('File uploaded and processed successfully!')
    })
    .catch((err) => {
      console.error(err)
      alert('Upload failed. Check server console for details.')
    })
  }
  </script>
  
  <style scoped>
  .drop-zone {
    border: 2px dashed #ccc;
    border-radius: 10px;
    padding: 2rem;
    text-align: center;
    background-color: #f9f9f9;
    cursor: pointer;
  }
  </style>
  