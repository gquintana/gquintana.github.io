require "rspec"
require "rspec/collection_matchers"
require_relative "../lib/fizzbuzz"

RSpec.describe FizzBuzz do
  describe "run" do
    it "returns number" do
      fizzbuzz = FizzBuzz.new
      expect(fizzbuzz.run(1)).to eq("1")
      expect(fizzbuzz.run(2)).to eq("2")
      expect(fizzbuzz.run(4)).to eq("4")
      expect(fizzbuzz.run(7)).to eq("7")
      expect(fizzbuzz.run(8)).to eq("8")
      expect(fizzbuzz.run(11)).to eq("11")
    end
    it "returns Fizz when multiple of 3" do
      fizzbuzz = FizzBuzz.new
      expect(fizzbuzz.run(3)).to eq("Fizz")
      expect(fizzbuzz.run(6)).to eq("Fizz")
      expect(fizzbuzz.run(9)).to eq("Fizz")
      expect(fizzbuzz.run(12)).to eq("Fizz")
      expect(fizzbuzz.run(18)).to eq("Fizz")
      expect(fizzbuzz.run(21)).to eq("Fizz")
    end
    it "returns Buzz when multiple of 5" do
      fizzbuzz = FizzBuzz.new
      expect(fizzbuzz.run(5)).to eq("Buzz")
      expect(fizzbuzz.run(10)).to eq("Buzz")
      expect(fizzbuzz.run(20)).to eq("Buzz")
      expect(fizzbuzz.run(25)).to eq("Buzz")
    end
    it "returns FizzBuzz when multiple of 3 and 5" do
      fizzbuzz = FizzBuzz.new
      expect(fizzbuzz.run(15)).to eq("FizzBuzz")
      expect(fizzbuzz.run(30)).to eq("FizzBuzz")
    end
  end
  describe "run_until" do
    it "returns number" do
      fizzbuzz = FizzBuzz.new
      result = fizzbuzz.run_until(100)
      expect(result).not_to be_empty
      expect(result).to have(100).result

      expect(result[0]).to eq("1")
      expect(result[2]).to eq("Fizz")
      expect(result[3]).to eq("4")
      expect(result[4]).to eq("Buzz")
      expect(result[8]).to eq("Fizz")
      expect(result[9]).to eq("Buzz")
      expect(result[14]).to eq("FizzBuzz")
      expect(result[29]).to eq("FizzBuzz")
    end
  end
end