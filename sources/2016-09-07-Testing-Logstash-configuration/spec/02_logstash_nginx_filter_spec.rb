# encoding: utf-8
require "logstash/devutils/rspec/spec_helper"

# Load the configuration file
@@configuration = String.new
@@configuration << File.read("conf/02_logstash_nginx_filter.conf")

describe "Nginx filter" do

  config(@@configuration) # <1>

  # Inject input event/message into the pipeline
  message = "172.17.0.1 - - [05/Sep/2016:20:06:17 +0000] \"GET /images/logos/hubpress.png HTTP/1.1\" 200 5432 \"http://localhost/\" \"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/51.0.2704.79 Chrome/51.0.2704.79 Safari/537.36\" \"-\""
  sample("message" => message, "type" => "nginx") do # <2>
    # Check the ouput event/message properties
    # Notice that (1), (2) and (3) are annotations and not ruby code
    insist { subject.get("type") } == "nginx" # <3>
    insist { subject.get("@timestamp").to_iso8601 } == "2016-09-05T20:06:17.000Z"
    insist { subject.get("verb") } == "GET"
    insist { subject.get("request") } == "/images/logos/hubpress.png"
    insist { subject.get("response") } == 200
    insist { subject.get("bytes") } == 5432
    reject { subject.get("tags").include?("_grokparsefailure") }
    reject { subject.get("tags").include?("_dateparsefailure") }
  end
end
