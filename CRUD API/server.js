const express = require("express");
const mongoose = require("mongoose");

const app = express();
app.use(express.json());

// MongoDB Connection
mongoose.connect("mongodb://127.0.0.1:27017/studentDB")
.then(() => console.log("MongoDB Connected"))
.catch(err => console.log(err));

// Schema
const studentSchema = new mongoose.Schema({
    name: String,
    age: Number
});

// Model
const Student = mongoose.model("Student", studentSchema);


// ---------------- CRUD APIs ----------------

// 1. CREATE
app.post("/students", async (req, res) => {
    const student = new Student(req.body);
    await student.save();
    res.send(student);
});


// 2. READ
app.get("/students", async (req, res) => {
    const students = await Student.find();
    res.send(students);
});


// 3. UPDATE
app.put("/students/:id", async (req, res) => {
    const updatedStudent = await Student.findByIdAndUpdate(
        req.params.id,
        req.body,
        { new: true }
    );

    res.send(updatedStudent);
});


// 4. DELETE
app.delete("/students/:id", async (req, res) => {
    await Student.findByIdAndDelete(req.params.id);
    res.send("Student Deleted");
});


// Server
app.listen(3000, () => {
    console.log("Server running on port 3000");
});