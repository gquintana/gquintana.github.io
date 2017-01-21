package main

import "fmt"

// tag::main[]
type Person struct {
	Name string
	Age  int
}

// NewPerson Constructor of Person
func NewPerson(name string) *Person {              // <1>
	return &Person{name, 0}
}

// GetName Function on Person
func (p *Person) GetName() string {                // <1>
	return p.Name
}

// Nameable interface
type Nameable interface {                          // <2>
	GetName() string
}

// SayName Function taking an interface as argument
func SayName(n Nameable) {                         // <3>
	fmt.Printf("My name is %s\n", n.GetName())
}

func main() {
	p := NewPerson("John")
	p.Age = 12
	SayName(p)                                 // <4>
}

// tag::main[]
