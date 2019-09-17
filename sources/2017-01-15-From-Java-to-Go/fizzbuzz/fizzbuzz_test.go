package fizzbuzz

import "testing"

func assertStringEquals(t *testing.T, expected, actual string) {
  if expected != actual {
    t.Errorf("Expected string %s but was %s", expected, actual)
  }
}

func TestFizzBuzz_Number(t *testing.T) {
  assertStringEquals(t, "1", FizzBuzz(1))
  assertStringEquals(t, "2", FizzBuzz(2))
  assertStringEquals(t, "4", FizzBuzz(4))
  assertStringEquals(t, "7", FizzBuzz(7))
  assertStringEquals(t, "8", FizzBuzz(8))
}

func TestFizzBuzz_Fizz(t *testing.T) {
  assertStringEquals(t, "Fizz", FizzBuzz(3))
  assertStringEquals(t, "Fizz", FizzBuzz(6))
  assertStringEquals(t, "Fizz", FizzBuzz(9))
  assertStringEquals(t, "Fizz", FizzBuzz(12))
}

func TestFizzBuzz_Buzz(t *testing.T) {
  assertStringEquals(t, "Buzz", FizzBuzz(5))
  assertStringEquals(t, "Buzz", FizzBuzz(10))
  assertStringEquals(t, "Buzz", FizzBuzz(20))
}

func TestFizzBuzz_FizzBuzz(t *testing.T) {
  assertStringEquals(t, "FizzBuzz", FizzBuzz(15))
  assertStringEquals(t, "FizzBuzz", FizzBuzz(30))
}