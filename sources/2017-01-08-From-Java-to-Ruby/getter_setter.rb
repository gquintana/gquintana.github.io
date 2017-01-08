class Person
  def name            # <1>
    @name
  end
  def name=(new_name) # <2>
    @name=new_name
  end
end

p=Person.new
p.name="John Doe"     # <3>
puts p.name           # <4>
