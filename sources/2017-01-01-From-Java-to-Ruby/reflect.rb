class Talkative
  def say_hello
    puts "Hello"
  end
  def method_missing(method_name, *arguments, &block)
    puts "Call #{method_name} with #{arguments}"
  end
end

s = Talkative.new
s.say_hello
s.say("Goodbye")
