plugins {
    java
    // groovy
    // kotlin("jvm") version "1.3.72"
    checkstyle
    distribution
    maven
    id("org.omegat.gradle") version "1.4.2"
}

version = "0.0.1"

omegat {
    version = "5.4.1"
//    pluginClass = "org.omegat.plugins.baidu.BaiduTranslate"
    pluginClass = "org.omegat.plugins.niu.NiuTranslate"
//    pluginClass = "org.omegat.plugins.volc.VolcTranslate"
//    pluginClass = "org.omegat.plugins.ali.AliTranslate"
}
tasks.jar {
//    archiveFileName.set("baidu-translate-plugin-0.0.1.jar")
    archiveFileName.set("niu-translate-plugin-0.0.1.jar")
//    archiveFileName.set("volc-translate-plugin-0.0.1.jar")
//    archiveFileName.set("ali-translate-plugin-0.0.1.jar")
}

dependencies {
    packIntoJar("org.slf4j:slf4j-api:1.7.21")
//    packIntoJar("com.volcengine:volc-sdk-java:1.0.98")
//    packIntoJar("com.aliyun:alimt20181012:1.0.3")
//    packIntoJar("com.aliyun:tea-openapi:0.2.5")
    implementation("com.aliyun:alimt20181012:1.0.3")
    implementation("com.aliyun:tea-openapi:0.2.5")
    implementation("com.volcengine:volc-sdk-java:1.0.98")
    implementation("commons-io:commons-io:2.5")
    implementation("commons-lang:commons-lang:2.6")
    implementation("org.slf4j:slf4j-nop:1.7.21")
    packIntoJar("com.squareup.okhttp3:okhttp:4.10.0")
    packIntoJar("com.alibaba:fastjson:1.2.17")
    testImplementation("junit:junit:4.12")
    testImplementation("xmlunit:xmlunit:1.6")
    testImplementation("org.madlonkay.supertmxmerge:supertmxmerge:2.0.1")
}

checkstyle {
    isIgnoreFailures = true
    toolVersion = "7.1"
}