package main

import "fmt"

func main() {
	a := [4]string{"One", "Two", "Three"}
	a[0] = "First"
	fmt.Println(a)
}
