# Write a program that prints the numbers from 1 to 100.
# But for multiples of three print “Fizz” instead
# of the number and for the multiples of five print “Buzz”.
# For numbers which are multiples of both three and five print “FizzBuzz”.
class FizzBuzz
  def run(input)
    if input%15 == 0
      return "FizzBuzz"
    elsif input%3 == 0
      return "Fizz"
    elsif input%5 == 0
      return "Buzz"
    end
    return input.to_s
  end
  def run_until(max)
    (1..max).map { |x| self.run(x)}
  end
end