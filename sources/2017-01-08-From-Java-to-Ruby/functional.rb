# tag::simple[]
twice = lambda { |x| 2 * x } # <1>
puts twice.call(3) # <2>

twice = lambda do |x| # <3>
  2 * x
end
# end::simple[]


def multiple(factor)
  return lambda { |x| factor * x }
end

twice =  multiple(2)
puts twice.class
puts twice.call(5)