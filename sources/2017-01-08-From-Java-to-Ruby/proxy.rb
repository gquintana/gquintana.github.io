class Proxy
  def initialize(target_object)
    @object = target_object
  end
  def method_missing(method_name, *arguments, &block)
    if @object.respond_to?(method_name)                      # <1>
      puts "Before #{method_name} #{arguments}"
      result = @object.send(method_name, *arguments, &block) # <2>
      puts "After #{method_name}: #{result}"
      return result
    end
  end
end

s = Proxy.new("Hello world!")                                # <3>
puts s.upcase