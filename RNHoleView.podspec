require 'json'
version = JSON.parse(File.read('package.json'))["version"]

Pod::Spec.new do |s|
  s.name         = "RNHoleView"
  s.version      = version
  s.summary      = "RNHoleView"
  s.description  = <<-DESC
  RNHoleView
  DESC
  s.homepage     = "ibitcy.com"
  s.license      = "MIT"
  s.author       = { "author" => "stepan.kopylov@ibitcy.com" }
  s.platform     = :ios, "9.0"
  s.source       = { :git => "https://github.com/ibitcy/react-native-hole-view", :tag => "master" }
  s.source_files = "ios/src/*.{h,m}"
#   s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES' }

  s.dependency 'React'
end

