<template>
<b-navbar toggleable="md" type="dark" variant="info">

  <b-nav-toggle target="nav_collapse"></b-nav-toggle>

  <b-navbar-brand href="#">Doggie</b-navbar-brand>

  <b-collapse is-nav id="nav_collapse">

    <b-nav is-nav-bar>
      <b-nav-item href="#">Not Connected</b-nav-item>
    </b-nav>

    <!-- Right aligned nav items -->
    <b-nav is-nav-bar class="ml-auto">

      <b-nav-form>
        <b-form-input size="sm" class="mr-sm-2" type="text" placeholder="Input address to search"/>
        <b-button size="sm" class="my-2 my-sm-0" type="submit">Search</b-button>
      </b-nav-form>

      <b-nav-item-dropdown text="Setting" right>
        <b-dropdown-item href="#">EN</b-dropdown-item>
        <b-dropdown-item href="#">ES</b-dropdown-item>
        <b-dropdown-item href="#">RU</b-dropdown-item>
        <b-dropdown-item href="#">FA</b-dropdown-item>
      </b-nav-item-dropdown>

      <b-nav-item-dropdown right>
        <!-- Using button-content slot -->
        <template slot="button-content">
          <em>User</em>
        </template>
        <b-dropdown-item href="#">Profile</b-dropdown-item>
        <b-dropdown-item href="#">Signout</b-dropdown-item>
      </b-nav-item-dropdown>
    </b-nav>

  </b-collapse>
</b-navbar>
</template>

<script>
export default {
  name: 'DoggieNavBar'
}
</script>

<style>

</style>
