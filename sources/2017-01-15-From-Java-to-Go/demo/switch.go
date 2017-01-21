package main

import (
	"fmt"
)

func patternMatch(i int) {
	// tag::pattern[]
	switch {
	case i == 0:
		fmt.Print("None")
	case i == 1:
		fmt.Print("One ")
	case i > 1:
		fmt.Print("Many ")
	default:
		fmt.Print("Invalid")
	}
	// end::pattern[]
}

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
