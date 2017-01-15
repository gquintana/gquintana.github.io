package main

import (
	"fmt"
	"math/rand"
)

func main() {
  // tag::for[]
	for i := 0; i < 10; i++ {
  // end::for[]
		// tag::main[]
		if dice := rand.Int31n(6) + 1; dice > 4 { // <1>
			fmt.Printf("%d You won!", dice)
		} else {
			fmt.Printf("%d You lost!", dice)
		}
		// end::main[]

		fmt.Println()
	}
}
