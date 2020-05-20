
Pod::Spec.new do |s|
  s.name         = "RNHoleView"
  s.version      = "0.0.1"
  s.summary      = "RNHoleView"
  s.description  = <<-DESC
  RNHoleView
  DESC
  s.homepage     = "ibitcy.com"
  s.license      = "MIT"
  # s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author       = { "author" => "stepan.kopylov@ibitcy.com" }
  s.platform     = :ios, "9.0"
  s.source       = { :git => "https://github.com/ibitcy/react-native-hole-view", :tag => "master" }
  s.source_files = "ios/src/*.{h,m}"

  s.dependency 'React'
end

