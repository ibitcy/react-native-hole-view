require 'json'

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-hole-view"
  s.version      = package['version']
  s.summary      = package['description']
  s.description  = package['description']
  s.homepage     = "ibitcy.com"
  s.license      = "MIT"
  s.author       = { "author" => "stepan.kopylov@ibitcy.com" }
  s.platforms    = { :ios => "11.0" }
  s.source       = { :git => "https://github.com/ibitcy/react-native-hole-view", :tag => "master" }
  s.source_files = "ios/**/*.{h,m,mm}"
  s.requires_arc     = true
  s.dependency 'React-Core'

  if ENV["RCT_NEW_ARCH_ENABLED"] == "1"
    install_modules_dependencies(s)
  end
end
