require "rspec"
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
  end
end