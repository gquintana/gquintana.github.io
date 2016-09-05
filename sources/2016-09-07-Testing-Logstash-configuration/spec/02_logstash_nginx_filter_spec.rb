# encoding: utf-8
require "logstash/devutils/rspec/spec_helper"

file = "conf/02_logstash_nginx_filter.conf"
@@configuration = String.new
@@configuration << File.read(file)

describe "Nginx filter" do

  config(@@configuration)

  message = "172.17.0.1 - - [05/Sep/2016:20:06:17 +0000] \"GET /images/logos/hubpress.png HTTP/1.1\" 200 5432 \"http://localhost/\" \"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/51.0.2704.79 Chrome/51.0.2704.79 Safari/537.36\" \"-\""

  sample("message" => message, "type" => "nginx") do
    insist { subject["type"] } == "nginx"
    #insist { subject["@timestamp"] } == "2016-09-05T19:41:12.000Z"
    insist { subject["verb"] } == "GET"
    insist { subject["request"] } == "/images/logos/hubpress.png"
    insist { subject["response"] } == 200
    insist { subject["bytes"] } == 5432
  end
end
