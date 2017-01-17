package main

import (
	"bufio"
	"fmt"
	"os"
	"strings"
)

func main() {
	// tag::main[]
	var line = "start"
	reader := bufio.NewReader(os.Stdin)
	for line != "quit" {                 // <1>
		fmt.Println(line)
		line, _ = reader.ReadString('\n')
		line = strings.TrimSpace(line)     // <2>
	}
	// end::main[]
}
