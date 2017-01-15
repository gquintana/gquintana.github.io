package main

import (
	"bytes"
	"fmt"
)

// tag::main[]
import 	"io/ioutil"                                        // <1>

func main() {
	data, err := ioutil.ReadFile("subpackage.go")            // <2>
  // end::main[]

	if err != nil {
		fmt.Errorf("Failed to read file: %s", err)
	}
	fmt.Printf("Read %d bytes", len(data))
	buffer := bytes.NewBuffer(data)
	fmt.Print(buffer.String())
}
