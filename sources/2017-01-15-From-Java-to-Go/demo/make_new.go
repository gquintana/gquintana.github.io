package main

import (
	"errors"
	"fmt"
)

type Language struct {
	Id   string
	Name string
}

func (l Language) String() string {
	return fmt.Sprintf("Language %s %s", l.Id, l.Name)
}

func main() {
	// tag::main[]
	// Structs
	struct1 := Language{"go", "Go Lang"}                      // <1>
	fmt.Println("struct1 ", struct1)
	struct2 := new(Language)                                  // <2>
	fmt.Println("struct2 ", struct2)

	// Arrays
	array1 := [5]Language{struct1, *struct2, Language{"java", "Java"}} // <3>
	fmt.Println("array1 ", array1)
	array2 := new([5]Language)                                // <4>
	fmt.Println("array2 ", array2)

	// Slices
	slice1 := array1[1:3]
	fmt.Println("slice1 ", slice1)
	slice2 := make([]Language, 1, 5)                          // <5>
	fmt.Println("slice2 ", slice2)

	// Maps
	map1 := map[string]Language{                              // <6>
		"go":   struct1,
		"java": Language{"java", "Java"},
	}
	fmt.Println("map1 ", map1)
	map2 := make(map[string]Language, 3)                      // <7>
	fmt.Println("map2 ", map2)

	// Other
	error1 := errors.New("Sample error")                      // <8>
	fmt.Println("error1 ", error1)
	// end::main[]
}
