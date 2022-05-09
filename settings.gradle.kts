rootProject.name = "public"

include(":RaidFinder")
include(":ItemUser")
include(":CannonReloader")
include(":PrayerpotDrinker")
include(":KeyClick")

for (project in rootProject.children) {
    project.apply {
        projectDir = file(name)
        buildFileName = "${name.toLowerCase()}.gradle.kts"

        require(projectDir.isDirectory) { "Project '${project.path} must have a $projectDir directory" }
        require(buildFile.isFile) { "Project '${project.path} must have a $buildFile build script" }
    }
}