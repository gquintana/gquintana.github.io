package main

import (
	"fmt"
)

func main() {
	i := 1
	// tag::main[]
	switch i {
	case 0:
		fmt.Print("None")
	case 1:
		fmt.Print("Single ")
		fallthrough
	default:
		fmt.Print("Thing")
	}
	// end::main[]
}
