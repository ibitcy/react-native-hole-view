require 'json'

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

folly_flags = '-DFOLLY_NO_CONFIG -DFOLLY_MOBILE=1 -DFOLLY_USE_LIBCPP=1 -Wno-comma -Wno-shorten-64-to-32'

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
    s.compiler_flags = folly_flags + " -DRCT_NEW_ARCH_ENABLED=1"
    s.pod_target_xcconfig    = {
      "HEADER_SEARCH_PATHS" => "\"$(PODS_ROOT)/boost\"",
      "OTHER_CPLUSPLUSFLAGS" => "-DFOLLY_NO_CONFIG -DFOLLY_MOBILE=1 -DFOLLY_USE_LIBCPP=1",
      "CLANG_CXX_LANGUAGE_STANDARD" => "c++17"
    }
    s.dependency "React-Codegen"
    s.dependency "React-RCTFabric"
    s.dependency "RCT-Folly"
    s.dependency "RCTRequired"
    s.dependency "RCTTypeSafety"
    s.dependency "ReactCommon/turbomodule/core"
  end
end

