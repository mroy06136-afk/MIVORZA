const { initializeTestEnvironment, assertFails, assertSucceeds } = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

const PROJECT_ID = "demo-no-project";
const DATABASE_ID = "ai-studio-android-niborja-00111f37-3d30-4d51-8ed2-96f564720176";

let testEnv;

before(async () => {
  const rules = fs.readFileSync("firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: "127.0.0.1",
      port: 8085
    }
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated access is denied by default", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc("user_1").get());
  await assertFails(unauthDb.collection("posts").add({ content: "spam" }));
});

test("Authenticated user can create their own profile", async () => {
  const aliceDb = testEnv.authenticatedContext("alice").firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc("alice").set({
      userId: "alice",
      displayName: "Alice",
      username: "alice_creator",
      bio: "Hello from NIBORJA!",
      createdAt: new Date(),
      updatedAt: new Date()
    })
  );
});

test("Authenticated user cannot create profile for another user ID", async () => {
  const aliceDb = testEnv.authenticatedContext("alice").firestore();
  await assertFails(
    aliceDb.collection("users").doc("bob").set({
      userId: "bob",
      displayName: "Bob",
      username: "bob_hacked",
      createdAt: new Date()
    })
  );
});

test("Authenticated user can create posts and read them", async () => {
  const aliceDb = testEnv.authenticatedContext("alice").firestore();
  await assertSucceeds(
    aliceDb.collection("posts").doc("post_1").set({
      userId: "alice",
      authorName: "Alice",
      authorUsername: "alice_creator",
      content: "First post on NIBORJA! #ConnectShareCreateEarn",
      mediaType: "none",
      likesCount: 0,
      commentsCount: 0,
      sharesCount: 0,
      audience: "public",
      createdAt: new Date()
    })
  );

  const bobDb = testEnv.authenticatedContext("bob").firestore();
  await assertSucceeds(bobDb.collection("posts").doc("post_1").get());
});

test("Non-author cannot delete another user's post", async () => {
  const aliceDb = testEnv.authenticatedContext("alice").firestore();
  await assertSucceeds(
    aliceDb.collection("posts").doc("post_2").set({
      userId: "alice",
      authorName: "Alice",
      content: "Alice's protected post",
      mediaType: "none",
      likesCount: 0,
      commentsCount: 0,
      sharesCount: 0,
      createdAt: new Date()
    })
  );

  const bobDb = testEnv.authenticatedContext("bob").firestore();
  await assertFails(bobDb.collection("posts").doc("post_2").delete());
});
