require 'json'
package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "RNHoleView"
  s.version      = package['version']
  s.summary      = package['description']
  s.description  = package['description']
  s.homepage     = "ibitcy.com"
  s.license      = "MIT"
  s.author       = { "author" => "stepan.kopylov@ibitcy.com" }
  s.platform     = :ios, "9.0"
  s.platforms    = { :ios => "9.0", :tvos => "9.2" }
  s.ios.deployment_target = "9.0"
  s.source       = { :git => "https://github.com/ibitcy/react-native-hole-view", :tag => "master" }
  s.source_files = "ios/src/*.{h,m}"
  s.requires_arc     = true
#   s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES' }

  s.dependency 'React-Core'

  if ENV['RCT_NEW_ARCH_ENABLED'] == '1' then
    s.compiler_flags = folly_compiler_flags + " -DRCT_NEW_ARCH_ENABLED=1"
    s.pod_target_xcconfig    = {
        "HEADER_SEARCH_PATHS" => "\"$(PODS_ROOT)/boost\"",
        "OTHER_CPLUSPLUSFLAGS" => "-DFOLLY_NO_CONFIG -DFOLLY_MOBILE=1 -DFOLLY_USE_LIBCPP=1",
        "CLANG_CXX_LANGUAGE_STANDARD" => "c++17"
    }

    s.dependency "React-RCTFabric"
    s.dependency "React-Codegen"
    s.dependency "RCT-Folly", folly_version
    s.dependency "RCTRequired"
    s.dependency "RCTTypeSafety"
    s.dependency "ReactCommon/turbomodule/core"
  end
end

