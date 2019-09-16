# tag::unless1[]
rude = false
unless rude    # <1>
  puts "Please"
end
# end::unless1[]

class Child
  def say_hello(who)
    puts "Hello #{who}"
  end
end
child = Child.new

# tag::unless2[]
rude = true
child.say_hello("mister") unless rude
# end::unless2[]
