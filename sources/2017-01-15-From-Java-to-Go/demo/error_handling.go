package main

import (
	"errors"
	"fmt"
)

type Article struct {
	Quantity   int
	TotalPrice int
}

func (art Article) Print() {
	fmt.Printf("Quantity %d Total price %d", art.Quantity, art.TotalPrice)
}

/*
func (self *Article, err error) Print2() error {
  if err != null {
    return err
  }
  fmt.Printf("Quantity %d Total price %d", self.Quantity, self.TotalPrice)
}
*/
// tag::main[]
func Order(quantity int) (*Article, error) {                 // <1>
	if quantity <= 0 {
		return nil, errors.New("Invalid quantity")               // <2>
	}
  return &Article{quantity, 10 * quantity}, nil              // <3>
}
func OrderAndPrint(quantity int) error {
	article, err := Order(3)
	if err != nil {
		return err                                               // <4>
	}
	article.Print()                                            // <5>
	return nil
}
func main() {
	err := OrderAndPrint(3)
	if err != nil {
		fmt.Printf("Error %s", err)                              // <5>
	}
}

// end::main[]
